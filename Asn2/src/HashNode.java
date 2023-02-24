
public class HashNode<K, V> {
	private K key;
	private V value;
	private int hash;
	
	private HashNode<K, V> next;
	
	public HashNode(K key, V value, int hash) {
		this.key = key;
		this.value = value;
		this.hash = hash;
	}
}
