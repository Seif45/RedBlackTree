package eg.edu.alexu.csd.filestructure.redblacktree;

import jdk.jshell.Snippet;

import javax.management.RuntimeErrorException;
import java.util.*;

public class TreeMap implements ITreeMap {

    private RedBlackTree treemap;
    private int size;
    private ArrayList<INode> allValues= new ArrayList();

    public TreeMap(){
        this.treemap=new RedBlackTree();
        this.size = 0;
    }

    @Override
    public Map.Entry ceilingEntry(Comparable key) {
        if(this.treemap.getRoot()==null){
            return null;
        }
        return Map.entry(ceilingKey(key), get(ceilingKey(key)));
    }

    @Override
    public Comparable ceilingKey(Comparable key) {
        if(key==null){
            throw new RuntimeErrorException(new Error());
        }
        INode node = this.treemap.getRoot();
        while(node != null){
            int cmp = key.compareTo(node.getKey());
            if(cmp<0){
                if(node.getLeftChild() != null){
                    node = node.getLeftChild();
                }else{
                    return node.getKey();
                }
            }else if(cmp>0){
                if(node.getRightChild() != null){
                    node = node.getRightChild();
                }else{
                    INode parent = node.getParent();
                    INode child = node;
                    while(parent != null && child == parent.getRightChild()){
                        child = parent;
                        parent = parent.getParent();
                    }
                    return parent.getKey();
                }
            }else{
                return node.getKey();
            }

        }
        return null;
    }

    @Override
    public void clear() {
        this.treemap.clear();
        this.size=0;
    }

    @Override
    public boolean containsKey(Comparable key) {
        if(key==null){
            throw new RuntimeErrorException(new Error());
        }
        if(this.treemap.getRoot()==null){
            return false;
        }
        return this.treemap.contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if(value==null){
            throw new RuntimeErrorException(new Error());
        }
        for(int i = 0; i<allValues.size(); i++){
            if(value.equals(allValues.get(i).getValue())){
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Map.Entry> entrySet() {
        ArrayList<Map.Entry> result = new ArrayList<>();
        for (int i = 0; i<allValues.size(); i++){
            Map.Entry temp = Map.entry(allValues.get(i).getKey(),allValues.get(i).getValue());
            result.add(temp);
        }
        Set<Map.Entry> Final = new LinkedHashSet(result);
        return Final;
    }

    @Override
    public Map.Entry firstEntry() {
        INode first = this.treemap.getRoot();
        if(first==null || size == 0){
            return null;
        }
        else if(!(first==null)){
            return Map.entry(firstKey(),get(firstKey()));
        }
        return null;
    }

    @Override
    public Comparable firstKey() {
        if(this.treemap.getRoot()==null || size == 0){
            return null;
        }
        INode p = treemap.getRoot();
        if (p != null)
            while (!p.getLeftChild().isNull())
                p = p.getLeftChild();
        return p.getKey();
    }

    @Override
    public Map.Entry floorEntry(Comparable key) {
        if(this.treemap.getRoot()==null){
            return null;
        }
        return Map.entry(floorKey(key), get(floorKey(key)));
    }

    @Override
    public Comparable floorKey(Comparable key) {
        if(key==null){
            throw new RuntimeErrorException(new Error());
        }
        INode node = this.treemap.getRoot();
        while (node != null) {
            int cmp = key.compareTo(node.getKey());
            if (cmp > 0) {
                if (node.getRightChild() != null)
                    node = node.getRightChild();
                else
                    return node.getKey();
            } else if (cmp < 0) {
                if (node.getLeftChild() != null) {
                    node = node.getLeftChild();
                } else {
                    INode parent = node.getParent();
                    INode child = node;
                    while (parent != null && child == parent.getLeftChild()) {
                        child = parent;
                        parent = parent.getParent();
                    }
                    return parent.getKey();
                }
            } else
                return node.getKey();

        }
        return null;
    }

    @Override
    public Object get(Comparable key) {
        if(this.treemap.getRoot()==null){
            return null;
        }
        else if(this.treemap.contains(key)){
            return this.treemap.search(key);
        }
        return null;
    }

    @Override
    public ArrayList<Map.Entry> headMap(Comparable toKey) {
        ArrayList<Map.Entry> result = new ArrayList();
        if(containsKey(toKey)){
            for(int i = 0; i<allValues.size();i++){
                INode tNode = allValues.get(i);
                Comparable tKey = tNode.getKey();
                if(tKey.compareTo(toKey)<0){
                    result.add(Map.entry(tNode.getKey(),tNode.getValue()));
                }else{
                    break;
                }
            }
        }else{
            result = headMap(floorKey(toKey), true);
        }
        return result;
    }

    @Override
    public ArrayList<Map.Entry> headMap(Comparable toKey, boolean inclusive) {
        ArrayList<Map.Entry> result = headMap(toKey);
        if(true) {
            result.add(Map.entry(toKey, get(toKey)));
        }
        return result;
    }

    @Override
    public Set keySet() {
        Set result = new TreeSet();
        for (int i = 0; i<allValues.size(); i++){
            result.add(allValues.get(i).getKey());
        }
        return result;
    }

    @Override
    public Map.Entry lastEntry() {
        INode last = this.treemap.getRoot();
        if(last==null || size == 0){
            return null;
        }
        else if(!(last==null)){
            return Map.entry(lastKey(),get(lastKey()));
        }
        return null;
    }

    @Override
    public Comparable lastKey() {
        if(this.treemap.getRoot()==null || size == 0){
            return null;
        }
        INode p = treemap.getRoot();
        if (p != null)
            while (!p.getRightChild().isNull())
                p = p.getRightChild();
        return p.getKey();
    }

    @Override
    public Map.Entry pollFirstEntry() {
        if(this.treemap.isEmpty()){
            return null;
        }
        Map.Entry first = firstEntry();
        remove(firstKey());
        return first;
    }

    @Override
    public Map.Entry pollLastEntry() {
        if(this.treemap.isEmpty()){
            return null;
        }
        Map.Entry last = lastEntry();
        remove(lastKey());
        return last;
    }

    @Override
    public void put(Comparable key, Object value) {
        if(containsKey(key)==true){
            this.treemap.insert(key, value);
            INode temp = new Node();
            temp.setKey(key);
            temp.setValue(value);
            for(int i= 0; i<allValues.size(); i++){
                if(allValues.get(i).getKey().compareTo(key)==0){
                    allValues.set(i,temp);
                    break;
                }
            }
        }else{
            this.treemap.insert(key, value);
            INode temp = new Node();
            temp.setKey(key);
            temp.setValue(value);
            allValues.add(temp);
            sort(allValues);
            this.size++;
        }
        sort(allValues);
    }

    @Override
    public void putAll(Map map) {
        if(map==null){
            throw new RuntimeErrorException(new Error());
        }
        Iterator<Integer> itr = map.keySet().iterator();
        while (itr.hasNext()){
            Integer temp = itr.next();
            put(temp,map.get(temp));
        }
    }

    @Override
    public boolean remove(Comparable key) {
        if(this.treemap.contains(key)){
            this.treemap.delete(key);
            size--;
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Collection values() {
        Collection result = new ArrayList();
        for (int i = 0; i<allValues.size(); i++){
            result.add(allValues.get(i).getValue());
        }
        return result;
    }
    private void sort(ArrayList<INode> list)
    {
        int n = list.size();
        for (int i = 1; i < n; ++i) {
            INode key = list.get(i);
            int j = i - 1;

            /* Move elements of arr[0..i-1], that are
               greater than key, to one position ahead
               of their current position */
            while (j >= 0 && list.get(j).getKey().compareTo(key.getKey())>0) {
                list.set(j + 1, list.get(j));
                j = j - 1;
            }
            list.set(j + 1, key);
        }
    }
}