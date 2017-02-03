package de.sb.broker.rest;

import java.lang.annotation.Annotation;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.sb.broker.model.Address;
import de.sb.broker.model.Auction;
import de.sb.broker.model.Bid;
import de.sb.broker.model.Contact;
import de.sb.broker.model.Document;
import de.sb.broker.model.Name;
import de.sb.broker.model.Person;
import de.sb.broker.model.Person.Group;

@Path("/people")
public class PersonService {
	private static final Object hash = null;
	private Person requester;


	/*
	 * GET /people: Returns the people matching the given criteria, with null or
	 * missing parameters identifying omitted criteria.
	 */
	@GET
	public List<Person> getPeople(@QueryParam("alias") String alias, @QueryParam("group") Group group,
			//@QueryParam("given") String given, 
			@QueryParam("address") Address address,
			@QueryParam("contact") Contact contact, @QueryParam("firstResult") int firstResult,
			@QueryParam("maxResults") int maxResults, @QueryParam("minCreationTimestamp") long minCreationTimestamp,
			@QueryParam("maxCreationTimestamp") long maxCreationTimestamp,
			@HeaderParam("Authorization") String authentication) {
		EntityManager em = LifeCycleProvider.brokerManager();
		LifeCycleProvider.authenticate(authentication);

		TypedQuery<Long> query = em.createQuery(
				"select p.identity from Person as p where" + "(:alias is null or p.alias = :alias) and"
						+ "(:group is null or p.group = :group) and" 
						+ "(:name is null or p.name = :name) and"
						+ "(:address is null or p.address = :address) and"
						+ "(:contact is null or p.contact = :contact) and"
						+ "(:maxCreationTimestamp is null or p.creationTimestamp <= :maxCreationTimestamp) and"
						+ "(:minCreationTimestamp is null or p.creationTimestamp <= :minCreationTimestamp) ",
				Long.class);

		query.setParameter("alias", alias);
		query.setParameter("group", group);
		//query.setParameter("name", name);
		query.setParameter("address", address);
		query.setParameter("contact", contact);
		query.setParameter("minCreationTimestamp", minCreationTimestamp);
		query.setParameter("maxCreationTimestamp", maxCreationTimestamp);
		if (maxResults > 0)
			query.setMaxResults(maxResults);
		if (firstResult > 0)
			query.setFirstResult(firstResult);

		List<Long> peopleIds = query.getResultList();
		List<Person> people = new ArrayList<Person>();

		// for-Schleife über alle IDs
		for (Long id : peopleIds) {
			people.add(em.find(Person.class, id));
		}

		// sort by alias
		// people.sort((a,b) -> a.getAlias().compareTo(b.getAlias()));

		people.sort(Comparator.comparing(Person::getAlias));

		return people;
	}

	/*
	 * GET /people/{identity}: Returns the person matching the given identity.
	 */
	@GET
	@Path("/{identity}")
	public Person getPerson(@HeaderParam("Authorization") final String authentication,
							@PathParam("identity") long identity) {
		EntityManager em = LifeCycleProvider.brokerManager();
		final Person requester = LifeCycleProvider.authenticate(authentication);

		Person person = em.find(Person.class, identity);

		return person;
	}

	/*
	 * GET /people/{identity}/auctions: Returns all auctions associated with the
	 * person matching the given identity (as seller or bidder).
	 */
	@GET
	@Path("/{identity}/auctions")
	public Response getSomeonesAuctions(
			@PathParam("identity") long identity, 
			@HeaderParam("Authorization") final String authentication,
			@QueryParam("seller") final boolean seller,
			@QueryParam("closed") final boolean closed
			) {//TODO Parameter ergänzen, zb closed
		EntityManager em = LifeCycleProvider.brokerManager();
		LifeCycleProvider.authenticate(authentication);

		Person person = em.find(Person.class, identity);

		// ArrayList<Auction> auctionsArray = new ArrayList<Auction>();
		// auctionsArray.addAll(person.getAuctions());
		
		//TODO über alle Gebote die auctions holen & hinzufügen
		//Wenn alle Auktionen geschlossen, Gebote und deren Bieter zurückgeben/marshalen 
		//auch bei getpeople und get auctions
		
		List<Auction> auctionsArray = person.getBids()
				.stream()
				.map(Bid::getAuction)
				.collect(Collectors.toList());
		
		auctionsArray.addAll(person.getAuctions());
		

		// set comparator to compare by different values in case some have the
		// same result
		final Comparator<Auction> comparator = Comparator.comparingLong(Auction::getClosureTimestamp)
				.thenComparing(Auction::getCreationTimestamp).thenComparing(Auction::getIdentity);
		auctionsArray.sort(comparator);

		Auction[] result = auctionsArray.toArray(new Auction[0]);
		
		
		List<Annotation> filterAnnotations = Arrays.asList(
				new Auction.XmlSellerAsEntityFilter.Literal(),
				new Auction.XmlBidsAsEntityFilter.Literal(),
				new Bid.XmlBidderAsEntityFilter.Literal(),
				new Bid.XmlAuctionAsReferenceFilter.Literal()
				);
		return Response.ok().entity(result, filterAnnotations.toArray(new Annotation[0])).build();//TODO entity() nutzen um filter-annotationen zu übergeben

	}

	/*
	 * GET /people/{identity}/bids: Returns all bids for closed auctions
	 * associated with the bidder matching the given identity.
	 */
	@GET
	@Path("/{identity}/bids")
	public ArrayList<Bid> getSomeonesBids(
			@PathParam("identity") long identity, 
			@HeaderParam("Authorization") final String authentication
			) {
		EntityManager em = LifeCycleProvider.brokerManager();
		LifeCycleProvider.authenticate(authentication);

		Person person = em.find(Person.class, identity);
		// define getter
		ArrayList<Bid> bidsArray = new ArrayList<Bid>();
		bidsArray.addAll(person.getBids());

		// Rückgabe sortieren
		// bidsArray.sort( (a,b) -> Long.compare( a.getPrice(), b.getPrice() )
		// );
		bidsArray.sort(Comparator.comparing(Bid::getPrice).thenComparing(Bid::getCreationTimestamp)
				.thenComparing(Bid::getIdentity));

		return bidsArray;
	}

	/*
	 * PUT /{identity}: Creates or modifies a person
	 * 
	 * @PUT
	 * 
	 * @Consumes({"application/xml", "application/json"}) public Long putPerson(
	 * 
	 * @Valid Person p,
	 * 
	 * @HeaderParam("Authorization") final String authentication ){
	 * 
	 * EntityManager em = LifeCycleProvider.brokerManager(); final Person
	 * requester = LifeCycleProvider.authenticate(authentication); final boolean
	 * persist = p.getIdentity() == 0; final Person person;
	 * 
	 * try { /* if existent: update person else: create new person
	 *
	 * if(persist){ person = new Person(); } else if (requester.getGroup() ==
	 * Group.ADMIN || requester.getIdentity() == p.getIdentity()){ person =
	 * em.find(Person.class, p.getIdentity()); if (person == null) throw new
	 * NotFoundException(); } else { throw new ForbiddenException(); }
	 * 
	 * person.setAlias(p.getAlias());
	 * person.setPasswordHash(p.getPasswordHash());
	 * person.setGroup(p.getGroup()); person.setVersion(p.getVersion());
	 * 
	 * if (persist){ em.persist(person); } else { em.flush(); }
	 * 
	 * try { em.getTransaction().commit(); } finally {
	 * em.getTransaction().begin(); }
	 * 
	 * } catch (ConstraintViolationException e) { throw new
	 * ClientErrorException(Status.BAD_REQUEST); } catch (RollbackException e) {
	 * throw new ClientErrorException(Status.CONFLICT); }
	 * 
	 * return p.getIdentity(); }
	 * 
	 */

	/*
	 * GET /people/requester (new): Returns the authenticated requester, which
	 * is useful for login operations.
	 */
	@GET
	@Path("/requester")
	@Produces({ "application/xml", "application/json" })
	public Person getRequester(@HeaderParam("Authorization") final String authentication) {
		return LifeCycleProvider.authenticate(authentication);
	}

	/*
	 * PUT /people: Requesters that are not part of group ADMIN are both
	 * forbidden to alter other people, and to set their own group to ADMIN.
	 */
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Long createOrUpdatePerson(@Valid @NotNull Person template,
			@HeaderParam("Authorization") final String authentication, @HeaderParam("Password") final String password) {

		final EntityManager brokerManager = LifeCycleProvider.brokerManager();
		final Person requester = LifeCycleProvider.authenticate(authentication);

		final boolean persist = template.getIdentity() == 0;
		final Person person;
		if (persist) {
			person = new Person();
		} else if (requester.getGroup() == Group.ADMIN || requester.getIdentity() == template.getIdentity()) {
			person = brokerManager.find(Person.class, template.getIdentity());
			if (person == null)
				throw new NotFoundException();
		} else {
			throw new ForbiddenException();
		}

		person.setAlias(template.getAlias());
		person.setGroup(template.getGroup());
		person.getName().setFamily(template.getName().getFamily());
		person.getName().setGiven(template.getName().getGiven());
		person.getAddress().setCity(template.getAddress().getCity());
		person.getAddress().setPostCode(template.getAddress().getPostCode());
		person.getAddress().setStreet(template.getAddress().getStreet());
		person.getContact().setEmail(template.getContact().getEmail());
		person.getContact().setPhone(template.getContact().getPhone());
		person.setPasswordHash(Person.passwordHash(password));
		person.setVersion(template.getVersion());

		try {
			if (persist)
				brokerManager.persist(person);
			else {
				brokerManager.flush();
			}
		} catch (ConstraintViolationException e) {
			throw new ClientErrorException(Status.BAD_REQUEST);
		}

		try {
			brokerManager.getTransaction().commit();
		} catch (RollbackException e) {
			throw new ClientErrorException(Status.CONFLICT);
		} finally {
			brokerManager.getTransaction().begin();
		}
		return person.getIdentity();
	}

	// GET /services/people/4711/avatar : avatar als dokument zurück
	// liefern
	// byte[] und mimetype/contenttype
	// return responsebuilder.ok(content, contenttype).build();

	@GET
	@Path("{identity}/avatar")
	@Produces(MediaType.WILDCARD)
	public Response getAvatar(@HeaderParam("Authorization") final String authentication,
			@PathParam("identity") final long id, @Min(1) @QueryParam("x") Integer x, @Min(1) @QueryParam("y") Integer y) {

		System.out.println("weder noch hallo");
		
		LifeCycleProvider.authenticate(authentication);

		EntityManager em = LifeCycleProvider.brokerManager();

		Person person = em.find(Person.class, id);
		
		
		Document document = person.getAvatar();


		if (document == null) {
			System.out.println("hallo");
			return Response.status(Status.NOT_FOUND).build();
		} else {

			System.out.println("elses hallo");
			
			if(x == null || y == null){
				return Response.ok(document.getContent(), document.getType()).build();
			}
			
			/*
			 * TODO
			 * resize auf x und y
			 * 
			 * und dann return
			 */
			return null;
		}
	}


	// PUT /services/people/4711/avatar : dokument hochladen (img)
	// beide mit @consume bzw. @produces mit wildcard(schaltet marshaling aus)
	// argument: mimetype/contenttype mit @headerparam("Content-type")
	@PUT
	@Path("{identity}/avatar")
	@Consumes(MediaType.WILDCARD)
	public void setAvatar(@HeaderParam("Authorization") final String authentication,
			@PathParam("identity") final long id, byte[] content, @QueryParam("Content-type") String contentType, String AVATAR_QUERY ) throws NoSuchAlgorithmException
			{
		LifeCycleProvider.authenticate(authentication);

		EntityManager em = LifeCycleProvider.brokerManager();
		
		byte [] contethash = MessageDigest.getInstance("SHA-256").digest(content);

		Person person = em.find(Person.class, id);
		
		if (person == null) throw new NotFoundException();
		
		if (!requester.equals(person) && requester.getGroup() != Person.Group.ADMIN){
			throw new ClientErrorException(Status.FORBIDDEN);
			
		}
		
		Document avatar;
		
		try{
			TypedQuery<Long> query = em.createQuery(AVATAR_QUERY, Long.class)
					.setParameter("contenthash", hash);
			
			Long documentIdentity =query.getSingleResult();
			avatar = em.find(Document.class, documentIdentity);
			
			try{
				avatar.setType(contentType);
				em.getTransaction().commit();
			}finally{
				if(em.getTransaction().isActive()){
					em.getTransaction().rollback();
				}
				em.getTransaction().begin();
			}
			
		}catch (Exception noDocumet){
			avatar=new Document(content);
		}
		
		
		
		
		
		
		/*TODO
		 * sha256 hash für content 
		 * query mit dem hash 
		 * select document.identity from Document as document where document.contentHash = :contentHash
		 * 
		 * wenn vorhanden, dann 
		 * person.setAvatar(document) //em. find....
		 * 
		 * wenn nicht, dann
		 * document erzeugen
		 * final Document document = new Document(content);
		 * document.setContentType(contentType);
		 * 
		 * em.persist(document);
		 * em.getTransaction().commit.... +finally{ begin ...;
		 */
	}


}

