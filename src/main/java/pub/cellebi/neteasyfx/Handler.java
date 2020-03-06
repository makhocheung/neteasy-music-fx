package pub.cellebi.neteasyfx;


@FunctionalInterface
public interface Handler<T> {
    void handle(T t) throws Exception;
}
