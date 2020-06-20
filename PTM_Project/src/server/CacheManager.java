package server;

public interface CacheManager<P, S> {
	public boolean Search(P problem);
	public void addCache(P problem, S solution);
	public S getCache(P problem);
}
