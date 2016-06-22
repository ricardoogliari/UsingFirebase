package estudo.example.com.usingfirebase;

/**
 * Created by ricardoogliari on 6/22/16.
 */

public class Task {

    public long termino;
    public String nome;
    public String descricao;
    public boolean iniciada;
    public String local;

    @Override
    public String toString() {
        return nome;
    }
}
