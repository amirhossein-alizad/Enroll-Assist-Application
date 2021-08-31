package ir.proprog.enrollassist.Exception;

import lombok.SneakyThrows;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExceptionList extends Exception{
    private final List<Exception> exceptions = new ArrayList<>();

    public ExceptionList() {}

    public void addNewException(Exception exception) {
        this.exceptions.add(exception);
    }

    public boolean hasException() {
        return this.exceptions.size() > 0;
    }

    public void addExceptions(List<Exception> newExceptions) {

        this.exceptions.addAll(newExceptions);
    }

    public List<Exception> getExceptions() {
        return this.exceptions;
    }

    @SneakyThrows
    @Override
    public String toString() {
        JSONObject exceptionListJson = new JSONObject();
        for (int i=0; i<this.exceptions.size(); i++)
            exceptionListJson.put(Integer.toString(i+1), this.exceptions.get(i).getMessage());
        return exceptionListJson.toString();
    }

}
