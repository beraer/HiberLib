package manager;

import Repository.PublisherRepo;
import jakarta.persistence.EntityManager;
import table.Publisher;

import java.util.List;
import java.util.Optional;

public class ManagePublisher {
    private final PublisherRepo publisherRepo;

    public ManagePublisher(EntityManager entityManager) {
        this.publisherRepo = new PublisherRepo(entityManager);
    }

    /**
     * Creates a new publisher
     * @param publisher The publisher to create
     * @return The created publisher
     * @throws Exception if a publisher with the same name already exists
     */
    public Publisher createPublisher(Publisher publisher) throws Exception {
        Optional<Publisher> existingPublisher = publisherRepo.findByExactName(publisher.getName());
        if (existingPublisher.isPresent()) {
            throw new Exception("Publisher already exists with name: " + publisher.getName());
        }

        return publisherRepo.save(publisher);
    }

    /**
     * Retrieves all publishers from the database.
     *
     * @return A list of all publishers.
     */
    public List<Publisher> getAllPublishers() {
        return publisherRepo.findAll();
    }
}