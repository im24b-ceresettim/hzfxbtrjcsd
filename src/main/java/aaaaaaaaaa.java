public abstract class AbstractPersistor<T> implements AutoCloseable {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private final EntityManagerFactory emf;

    protected AbstractPersistor() {
        this.emf = Persistence.createEntityManagerFactory("localPU", Config.getProperties());
    }

    public void save(T t) {
        executeTransaction(em -> em.merge(t));
    }

    protected void executeTransaction(Consumer<EntityManager> action) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                action.accept(em);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                log.error("Error during transaction:", e);
                throw e;
            }
        }
    }

    @Override
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            log.info("EntityManagerFactory closed");
        }
    }