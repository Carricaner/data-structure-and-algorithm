package org.demo.datastructure.hashmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Ref:
 * https://www.geeksforgeeks.org/implementing-our-own-hash-table-with-separate-chaining-in-java/
 */
public class Map<K, V> {
    // bucketArray is used to store array of chains
    private List<HashNode<K, V> > bucket;

    // Current capacity of array list
    private int numBuckets;

    // Current size of array list
    private int size;

    // Constructor
    public Map() {
        bucket = new ArrayList<>();
        numBuckets = 10;
        size = 0;

        // Create empty chains
        for (int i = 0; i < numBuckets; i++)
            bucket.add(null);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    private int hashCode (K key) {
        return Objects.hashCode(key);
    }

    // This implements hash function to find index for a key
    private int getBucketIndex(K key) {
        int hashCode = hashCode(key);
        int index = hashCode % numBuckets;
        // Since hash code could be negative
        index = index < 0 ? index * -1 : index;
        return index;
    }

    // Method to remove a given key
    public V remove(K key) {
        // Apply hash function to find index for given key
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);
        // Get head of chain
        HashNode<K, V> head = bucket.get(bucketIndex);

        // Search for key in its chain
        HashNode<K, V> prev = null;
        while (head != null) {
            // If Key found
            if (head.getKey().equals(key) && hashCode == head.getHashCode())
                break;

            // Else keep moving in chain
            prev = head;
            head = head.next;
        }

        // If key was not there
        if (head == null) return null;

        // Reduce size
        size--;

        // Remove key
        if (prev != null)
            prev.next = head.next;
        else
            bucket.set(bucketIndex, head.next);

        return head.getValue();
    }

    // Returns value for a key
    public V get(K key) {
        // Find head of chain for given key
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);

        HashNode<K, V> head = bucket.get(bucketIndex);

        // Search key in chain
        while (head != null) {
            if (head.getKey().equals(key) && head.getHashCode() == hashCode)
                return head.getValue();
            head = head.next;
        }

        // If key not found
        return null;
    }

    // Adds a key value pair to hash
    public void add(K key, V value) {
        // Find head of chain for given key
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);
        HashNode<K, V> head = bucket.get(bucketIndex);

        // Check if key is already present
        while (head != null) {
            if (head.getKey().equals(key) && head.getHashCode() == hashCode) {
                head.setValue(value);
                return;
            }
            head = head.next;
        }

        // Insert key in chain
        size++;
        head = bucket.get(bucketIndex);
        HashNode<K, V> newNode = new HashNode<>(key, value, hashCode);
        newNode.next = head;
        bucket.set(bucketIndex, newNode);

        // If load factor goes beyond threshold, then
        // double hash table size
        if ((1.0 * size) / numBuckets >= 0.7) {
            List<HashNode<K, V> > temp = bucket;
            bucket = new ArrayList<>();
            numBuckets = 2 * numBuckets;
            size = 0;
            for (int i = 0; i < numBuckets; i++)
                bucket.add(null);

            for (HashNode<K, V> headNode : temp) {
                while (headNode != null) {
                    add(headNode.getKey(), headNode.getValue());
                    headNode = headNode.next;
                }
            }
        }
    }
}
