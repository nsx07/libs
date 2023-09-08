public class NullIdException extends Exception {

    @Override
    public String getMessage() {
        return "Para as entidades serem salvas, elas precisam passar pelo ServiceBase primeiramente.";
    }
}
