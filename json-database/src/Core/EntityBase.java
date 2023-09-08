import java.util.UUID;

public abstract class EntityBase {

    private UUID id;

    public EntityBase() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
