import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedSet;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T> {

        SkipListSet<T>.SkipListSetItem<T> currNode;

        @SuppressWarnings("unchecked")
        SkipListSetIterator(){
            currNode = (SkipListSet<T>.SkipListSetItem<T>) headNode;
        }

        @Override
        public boolean hasNext() {

            if (currNode == null) return false;
            
            if (currNode.nextConnections.get(0) != null){
                return true;
            }

            return false;
        }
    
        @Override
        public T next() {

            T data = currNode.nextConnections.get(0).data;

            currNode = currNode.nextConnections.get(0);

            return data;
        }
    
    }

    private class SkipListSetItem<T extends Comparable<T>> implements Comparable<T>{

        // Data in node
        T data;

        // Height of node
        int height;

        // Links to the right of current node
        ArrayList<SkipListSetItem<T>> nextConnections;

        // Constructor to create a new node given 
        SkipListSetItem(int height){

            this.height = height;

            this.data = null;

            this.nextConnections = new ArrayList<>();

            for (int i = 0; i < height; i++){

                this.nextConnections.add(null);
            }
        }

        SkipListSetItem(T data, int height){

            this.height = height;

            this.data = data;

            this.nextConnections = new ArrayList<>();

            for (int i = 0; i < height; i++){

                this.nextConnections.add(null);
            }
        }

        @Override
        public int compareTo(T o) {

            String temp1 = this.data.toString();
            String temp2 = o.toString();

            return temp1.compareTo(temp2);
        }

    }

    SkipListSetItem<T> headNode;
    int maxHeight;
    int size;

    SkipListSet(){
        this.headNode = new SkipListSetItem<>(8);
        this.maxHeight = 8;
        this.size = 0;
    }

    SkipListSet(Collection<? extends T> c){
        this.headNode = new SkipListSetItem<>(8);
        this.maxHeight = 8;
        this.size = 0;

        for (T data : c) {
            this.add(data);
        }
    }
    
    private int generateRandomHeight(){

        Random coin = new Random();

        int val = 1;

        int currHeight = 0;

        while (val == 1)
        {
            val = coin.nextInt(2);
            currHeight += 1;

            if (currHeight == maxHeight) break;
        }

        return currHeight;
    }
    
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        if (headNode.nextConnections.get(0) == null) return true;
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {

        int currLevel = this.maxHeight - 1;

        SkipListSetItem<T> currNode = this.headNode;

        while(currLevel >= 0){

            if(currNode.nextConnections.get(currLevel) == null){
                currLevel--;
            }
            else if(currNode.nextConnections.get(currLevel).data.compareTo((T) o) > 0){
                currLevel--;
            }
            else if(currNode.nextConnections.get(currLevel).data.compareTo((T) o) < 0){
                currNode = currNode.nextConnections.get(currLevel);
            }
            else if(currNode.nextConnections.get(currLevel).data.compareTo((T) o) == 0) return true;
        
        }

        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new SkipListSetIterator<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object[] toArray() {

        SkipListSetItem<T> currNode = this.headNode;
        SkipListSetItem<T> [] nodeArray = new SkipListSetItem[this.size + 1];
        ArrayList<SkipListSetItem<T>> nodeArray2 =  new ArrayList<>();
        int cnt = 0;

        while(currNode.nextConnections.get(0) != null){

            currNode = currNode.nextConnections.get(0);
            nodeArray[cnt] = currNode;
            nodeArray2.add(cnt, currNode);
            cnt++;
        }

        return nodeArray2.toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {

        Object [] arr = this.toArray();
        int cnt = 0;

        for (Object data : arr) {
            a[cnt] = (T) data;
        }

        return a;
    }

    public boolean reBalance(){

        // TODO
        //SkipListSetItem<T> prevNode = headNode;
        SkipListSetItem<T> currNode = headNode.nextConnections.get(0);

        while (currNode != null){

            T data = currNode.data;

            this.remove(data);
            this.add(data);
            //int newHeight = generateRandomHeight();

            //SkipListSetItem<T> temp = new SkipListSetItem<>(currNode.data, newHeight);

            //ArrayList<SkipListSetItem<T>> connections = new ArrayList<>();

            /*for (int i = 0; i < newHeight; i++){
                
                prevNode.nextConnections.set(i,temp);
                
                if (headNode.nextConnections.get(i) == null){
                    headNode.nextConnections.set(i,temp);
                }

                //connections.add(i, currNode.nextConnections.get(i));
            }*/

            currNode = currNode.nextConnections.get(0);
            //prevNode = prevNode.nextConnections.get(0);
        }

        return true;
    }

    @Override
    public int hashCode(){

        SkipListSetItem<T> currNode = headNode;
        int res = 0;

        while (currNode.nextConnections.get(0) != null){
            currNode = currNode.nextConnections.get(0);
            res += currNode.data.hashCode();
        }

        return res;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o){

        SkipListSetItem<T> currNode = headNode;

        if (o == null) return false;

        SkipListSet<T> other =  (SkipListSet<T>) o;
        SkipListSetItem<T> otherCurr = other.headNode;
        

        while (currNode.nextConnections.get(0) != null){

            if (currNode.data != otherCurr.data) return false;

            currNode = currNode.nextConnections.get(0);
            otherCurr = otherCurr.nextConnections.get(0);
        }

        return true;
    }

    @Override
    public boolean add(T e) {

        if (this.contains(e)) return false;

        // Check if maxheight needs changing
        int newMaxHeight = (int) Math.ceil(Math.log(size) / Math.log(2));

        int currMaxHeight = this.maxHeight;

        if (newMaxHeight > currMaxHeight){

            this.maxHeight = newMaxHeight;
            this.headNode.height = maxHeight;

            int diff = newMaxHeight - currMaxHeight;

            for (int i = 0; i < diff; i++){
                headNode.nextConnections.add(null);
            } 
        } 

        // Generate random height for new node
        int height = generateRandomHeight();

        SkipListSetItem<T> newNode = new SkipListSetItem<>(e, height);

        // If skiplist is empty then just add to the head
        if (this.isEmpty()){
            for(int i = 0; i < height; i++){
                headNode.nextConnections.set(i, newNode);
                newNode.nextConnections.set(i, null);
            }
            return true;
        }

        // Set current level to search to the top of the list
        int currLevel = maxHeight - 1;

        SkipListSetItem<T> currNode = headNode;
        ArrayList<SkipListSetItem<T>> connectors = new ArrayList<>();

        // Gather all connections need to be made to the new node
        for(int i = 0; i < maxHeight; i++) connectors.add(null);

        while(currLevel >= 0){

            if(currNode.nextConnections.get(currLevel) == null){

                connectors.set(currLevel, currNode.nextConnections.get(currLevel));
                if (currLevel < newNode.height){
                    currNode.nextConnections.set(currLevel, newNode);
                }
                currLevel--;
            }
            else if(currNode.nextConnections.get(currLevel).data.compareTo(e) > 0){

                connectors.set(currLevel, currNode.nextConnections.get(currLevel));
                if (currLevel < newNode.height){
                    currNode.nextConnections.set(currLevel, newNode);
                }
                currLevel--;
            }
            else if(currNode.nextConnections.get(currLevel).data.compareTo(e) < 0){

                currNode = currNode.nextConnections.get(currLevel);
            }
            
        }

        // Connect the new node to the nodes before and after it
        for (int i = 0; i < height; i++){

            newNode.nextConnections.set(i, connectors.get(i));
        }

        // Increase size of skiplist
        this.size++;

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {

        if (!this.contains(o)) return false;
        
        int currLevel = this.maxHeight - 1;

        SkipListSetItem<T> currNode = this.headNode;

        SkipListSetItem<T> delNode;

        while(currLevel >= 0){

            if(currNode.nextConnections.get(currLevel) == null){
                currLevel--;
            }
            else if(currNode.nextConnections.get(currLevel).data.compareTo((T) o) > 0){
                currLevel--;
            }
            else if(currNode.nextConnections.get(currLevel).data.compareTo((T) o) < 0){
                currNode = currNode.nextConnections.get(currLevel);
            }
            else if(currNode.nextConnections.get(currLevel).data.compareTo((T) o) == 0){

                delNode = currNode.nextConnections.get(currLevel);
                currNode.nextConnections.set(currLevel, delNode.nextConnections.get(currLevel));
                currLevel--;
            }
        
        }

        this.size--;

        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object data : c) {
            if (!this.contains(data)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T data : c) {
            this.add(data);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        
        SkipListSetItem<T> currNode = this.headNode;

        while (currNode.nextConnections.get(0) != null){
            currNode = currNode.nextConnections.get(0);
            if (!c.contains(currNode.data)) this.remove(currNode.data);
        }

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {

       for (Object data : c) {
            this.remove(data);
        }
        return true;
    }

    @Override
    public void clear() {
        
        this.headNode = new SkipListSetItem<>(8);
        this.maxHeight = 8;
        this.size = 0;

    }

    @Override
    public Comparator<? super T> comparator() {
        return null;
    }
    
    @Override
    public T first() {
        return headNode.nextConnections.get(0).data;
    }

    @Override
    public T last() {

        SkipListSetItem<T> currNode = headNode.nextConnections.get(0);

        while (currNode.nextConnections.get(0) != null){
            currNode = currNode.nextConnections.get(0);
        }
        return currNode.data;
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException("Unimplemented method 'headSet'");
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException("Unimplemented method 'tailSet'");
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException("Unimplemented method 'subSet'");
    }

}