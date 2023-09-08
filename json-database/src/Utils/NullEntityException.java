public class NullEntityException extends Exception {
    
    @Override
    public String getMessage() {
        return "A classe entityBase não pode ser instanciada com valores nulos ou vazios.";
    }

}
