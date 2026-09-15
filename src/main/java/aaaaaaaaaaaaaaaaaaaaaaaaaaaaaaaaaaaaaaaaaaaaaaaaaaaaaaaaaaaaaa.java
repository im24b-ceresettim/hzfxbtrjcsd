package ch.bzz.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "isbn", nullable = false, length = 20, unique = true)
    private String isbn;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "author", nullable = false, length = 255)
    private String author;

    @Column(name = "publication_year")
    private Integer publicationYear;

    public Book(){}

    public Book(Integer id, String isbn, String title, String author, Integer publicationYear) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("localPU", <PROPERTIES>);

    public List<Book> getAll(int limit) {
        try (EntityManager em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT b FROM Book b ORDER BY id", Book.class);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        }
    }


    public void saveAll(List<Book> books) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                books.forEach(em::merge);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                log.error("Error during saving of books to the database:", e);
            }
        }
    }