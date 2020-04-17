package eg.edu.alexu.csd.filestructure.redblacktree;

import javax.management.RuntimeErrorException;
import java.util.concurrent.BlockingDeque;

public class RedBlackTree <T extends Comparable<T>, V> implements IRedBlackTree<T, V> {

    private INode<T, V> root = new Node<>();

    public RedBlackTree (){

    }

    @Override
    public INode<T, V> getRoot() {
        return this.root;
    }

    @Override
    public boolean isEmpty() {
        return this.getRoot().isNull();
    }

    @Override
    public void clear() {
        this.root = new Node<>();
    }

    @Override
    public V search(T key) {
        if(key==null) {
            throw new RuntimeErrorException(new Error());
        }
        if (this.isEmpty()) return null;
        return searchChildren(this.getRoot(), key, null);
    }

    @Override
    public boolean contains(T key) {
        if(key==null) {
            throw new RuntimeErrorException(new Error());
        }
        if (this.isEmpty()) return false;
        return containsChildren(this.getRoot(), key, false);
    }

    @Override
    public void insert(T key, V value) {
        if(key == null || value == null){
            throw new RuntimeErrorException(new Error());
        }
        if (this.isEmpty()) {
            INode<T, V> newRoot = new Node<>();
            newRoot.setKey(key);
            newRoot.setValue(value);
            newRoot.setLeftChild(new Node<>());
            newRoot.setRightChild(new Node<>());
            this.root = newRoot;
            return;
        }
        INode<T, V> insertParent = insertBST(this.getRoot(), key);
        if (insertParent.getKey().compareTo(key) == 0) {
            insertParent.setValue(value);
            return;
        }
        INode<T, V> insertedChild = insertInto(insertParent, key, value);
        recolor(insertedChild);
    }

    @Override
    public boolean delete(T key) {
        if(key==null){
            throw new RuntimeErrorException(new Error());
        }
        if (this.isEmpty()) return false;
        INode<T, V> toDelete =  findNode(root, key);
        if (toDelete == null) return false;
        deleteBST(toDelete);
        return true;
    }

    private V searchChildren(INode<T, V> parent, T key, V value){
        if(key == null){
            return null;
        }
        if (parent.getKey().compareTo(key) > 0 && !parent.getLeftChild().isNull()) {
            value = searchChildren(parent.getLeftChild(), key, value);
        }
        else if (parent.getKey().compareTo(key) < 0 && !parent.getRightChild().isNull()) {
            value = searchChildren(parent.getRightChild(), key, value);
        }
        else if (parent.getKey().compareTo(key) == 0) {
            value = parent.getValue();
        }
        return value;
    }

    private boolean containsChildren(INode<T, V> parent, T key, boolean found){
        if (parent.getKey().compareTo(key) > 0 && !parent.getLeftChild().isNull()) found = containsChildren(parent.getLeftChild(), key, found);
        else if (parent.getKey().compareTo(key) < 0 && !parent.getRightChild().isNull()) found = containsChildren(parent.getRightChild(), key, found);
        else if (parent.getKey().compareTo(key)==0) return true;
        else found = false;
        return found;
    }

    private INode<T, V> insertBST(INode<T, V> parent, T key){
        if (parent.getKey().compareTo(key) > 0 && !parent.getLeftChild().isNull()){
            parent = insertBST(parent.getLeftChild(), key);
        }
        else if (parent.getKey().compareTo(key) < 0 && !parent.getRightChild().isNull()){
            parent = insertBST(parent.getRightChild(), key);
        }
        return parent;
    }

    private INode<T, V> findNode(INode<T, V> parent, T key){
        if (parent != null && parent.getLeftChild() != null && !parent.isNull() && parent.getKey().compareTo(key) > 0 && !parent.getLeftChild().isNull()) parent = findNode(parent.getLeftChild(), key);
        else if (parent != null && parent.getRightChild() != null && !parent.isNull() && parent.getKey().compareTo(key) < 0 && !parent.getRightChild().isNull()) parent = findNode(parent.getRightChild(), key);
        if (parent != null && !parent.isNull() && parent.getKey().compareTo(key) == 0){
            return parent;
        }
        return null;
    }

    private void deleteBST(INode<T, V> toDelete){
        if (toDelete.getLeftChild()!=null && toDelete.getRightChild()!=null && toDelete.getLeftChild().isNull() && toDelete.getRightChild().isNull()){
            toDelete.setKey(null);
            toDelete.setValue(null);
            toDelete.setLeftChild(null);
            toDelete.setRightChild(null);
            if (toDelete.getColor() == INode.RED){
                toDelete.setColor(INode.BLACK);
            }
            else {
                deleteBlack(toDelete);
            }
        }
        else if (toDelete.getRightChild()!=null && toDelete.getRightChild().isNull() && toDelete.getLeftChild() != null){
            toDelete.setKey(toDelete.getLeftChild().getKey());
            toDelete.setValue(toDelete.getLeftChild().getValue());
            deleteBST(toDelete.getLeftChild());
        }
        else if (toDelete.getRightChild()!=null){
            INode<T, V> min = toDelete.getRightChild();
            while(min.getLeftChild()!=null && !min.getLeftChild().isNull()){
                min = min.getLeftChild();
            }
            toDelete.setValue(min.getValue());
            toDelete.setKey(min.getKey());
            if (min.getRightChild()!=null && min.getRightChild().isNull()){
                min.setKey(null);
                min.setValue(null);
                min.setLeftChild(null);
                min.setRightChild(null);
                if (min.getColor() == INode.RED){
                    min.setColor(INode.BLACK);
                }
                else {
                    deleteBlack(min);
                }
            }
            else{
                deleteBST(min);
            }

        }
    }

    private void deleteBlack(INode<T, V> node){
        INode<T, V> parent = node.getParent();
        INode<T, V> sibling;
        if (parent != null && parent.getLeftChild() != null && parent.getRightChild() != null && parent.getLeftChild() == node){
            sibling = parent.getRightChild();
            if (sibling.getColor() == INode.BLACK){
                INode<T, V> redChild;
                if (sibling.getRightChild()!=null && sibling.getRightChild().getColor() == INode.RED){
                    redChild = sibling.getRightChild();
                    if (this.getRoot() != parent){
                        sibling.setParent(parent.getParent());
                        if (parent.getParent().getLeftChild() == parent){
                            parent.getParent().setLeftChild(sibling);
                        }
                        else {
                            parent.getParent().setRightChild(sibling);
                        }
                    }
                    else{
                        sibling.setParent(null);
                        this.root = sibling;
                    }
                    parent.setParent(sibling);
                    parent.setRightChild(sibling.getLeftChild());
                    if (sibling.getLeftChild() != null){
                        sibling.getLeftChild().setParent(parent);
                    }
                    sibling.setLeftChild(parent);
                    redChild.setColor(INode.BLACK);
                    boolean temp = parent.getColor();
                    parent.setColor(sibling.getColor());
                    sibling.setColor(temp);
                    if (this.getRoot() == sibling){
                        sibling.setColor(INode.BLACK);
                    }
                }
                else if (sibling.getLeftChild()!=null && sibling.getLeftChild().getColor() == INode.RED){
                    redChild = sibling.getLeftChild();
                    parent.setRightChild(redChild);
                    redChild.setParent(parent);
                    sibling.setLeftChild(redChild.getRightChild());
                    if (redChild.getRightChild() != null){
                        redChild.getRightChild().setParent(sibling);
                    }
                    redChild.setRightChild(sibling);
                    sibling.setParent(redChild);
                    sibling.setColor(INode.RED);
                    redChild.setColor(INode.BLACK);
                    if (this.getRoot() != parent){
                        redChild.setParent(parent.getParent());
                        if (parent.getParent().getLeftChild() == parent){
                            parent.getParent().setLeftChild(redChild);
                        }
                        else {
                            parent.getParent().setRightChild(redChild);
                        }
                    }
                    else{
                        redChild.setParent(null);
                        this.root = redChild;
                    }
                    parent.setParent(redChild);
                    parent.setRightChild(redChild.getLeftChild());
                    if (redChild.getLeftChild() != null){
                        redChild.getLeftChild().setParent(parent);
                    }
                    redChild.setLeftChild(parent);
                    sibling.setColor(INode.BLACK);
                    boolean temp = parent.getColor();
                    parent.setColor(redChild.getColor());
                    redChild.setColor(temp);
                    if (this.getRoot() == redChild){
                        redChild.setColor(INode.BLACK);
                    }
                }
                else if (sibling.getLeftChild() != null && sibling.getRightChild()!=null && sibling.getLeftChild().getColor()==INode.BLACK && sibling.getRightChild().getColor()==INode.BLACK){
                    sibling.setColor(INode.RED);
                    if (parent.getColor() == INode.BLACK){
                        deleteBlack(parent);
                    }
                    else {
                        parent.setColor(INode.BLACK);
                    }
                }
            }
            else{
                if (this.getRoot() != parent){
                    sibling.setParent(parent.getParent());
                    if (parent.getParent().getLeftChild() == parent){
                        parent.getParent().setLeftChild(sibling);
                    }
                    else{
                        parent.getParent().setRightChild(sibling);
                    }
                }
                else {
                    sibling.setParent(null);
                    this.root = sibling;
                }
                parent.setParent(sibling);
                parent.setRightChild(sibling.getLeftChild());
                if (sibling.getLeftChild() != null){
                    sibling.getLeftChild().setParent(parent);
                }
                sibling.setLeftChild(parent);
                sibling.setColor(INode.BLACK);
                parent.setColor(INode.RED);
                deleteBlack(node);
            }
        }
        else if (parent != null && parent.getRightChild() != null && parent.getLeftChild() != null && parent.getRightChild() == node) {
            sibling = parent.getLeftChild();
            if (sibling.getColor() == INode.BLACK){
                INode<T, V> redChild;
                if (!(sibling.getLeftChild()==null) && sibling.getLeftChild().getColor() == INode.RED){
                    redChild = sibling.getLeftChild();
                    if (this.getRoot() != parent){
                        sibling.setParent(parent.getParent());
                        if (parent.getParent().getLeftChild() == parent){
                            parent.getParent().setLeftChild(sibling);
                        }
                        else {
                            parent.getParent().setRightChild(sibling);
                        }
                    }
                    else{
                        sibling.setParent(null);
                        this.root = sibling;
                    }
                    parent.setParent(sibling);
                    parent.setLeftChild(sibling.getRightChild());
                    if (sibling.getRightChild() != null){
                        sibling.getRightChild().setParent(parent);
                    }
                    sibling.setRightChild(parent);
                    redChild.setColor(INode.BLACK);
                    boolean temp = parent.getColor();
                    parent.setColor(sibling.getColor());
                    sibling.setColor(temp);
                    if (this.getRoot() == sibling){
                        sibling.setColor(INode.BLACK);
                    }
                }
                else if (!(sibling.getRightChild()==null) && sibling.getRightChild().getColor() == INode.RED){
                    redChild = sibling.getRightChild();
                    parent.setLeftChild(redChild);
                    redChild.setParent(parent);
                    sibling.setRightChild(redChild.getLeftChild());
                    if (sibling.getLeftChild() != null){
                        sibling.getLeftChild().setParent(sibling);
                    }
                    if (redChild.getLeftChild() != null){
                        redChild.getLeftChild().setParent(sibling);
                    }
                    redChild.setLeftChild(sibling);
                    sibling.setParent(redChild);
                    sibling.setColor(INode.RED);
                    redChild.setColor(INode.BLACK);
                    if (this.getRoot() != parent){
                        redChild.setParent(parent.getParent());
                        if (parent.getParent().getLeftChild() == parent){
                            parent.getParent().setLeftChild(redChild);
                        }
                        else {
                            parent.getParent().setRightChild(redChild);
                        }
                    }
                    else{
                        redChild.setParent(null);
                        this.root = redChild;
                    }
                    parent.setParent(redChild);
                    parent.setLeftChild(redChild.getRightChild());
                    if (redChild.getRightChild() != null){
                        redChild.getRightChild().setParent(parent);
                    }
                    redChild.setRightChild(parent);
                    sibling.setColor(INode.BLACK);
                    boolean temp = parent.getColor();
                    parent.setColor(redChild.getColor());
                    redChild.setColor(temp);
                    if (this.getRoot() == redChild){
                        redChild.setColor(INode.BLACK);
                    }
                }
                else if (sibling.getLeftChild() != null && sibling.getRightChild()!=null && sibling.getLeftChild().getColor()==INode.BLACK && sibling.getRightChild().getColor()==INode.BLACK){
                    sibling.setColor(INode.RED);
                    if (parent.getColor() == INode.BLACK){
                        deleteBlack(parent);
                    }
                    else {
                        parent.setColor(INode.BLACK);
                    }
                }
            }
            else {
                if (this.getRoot() != parent){
                    sibling.setParent(parent.getParent());
                    if (parent.getParent().getLeftChild() == parent){
                        parent.getParent().setLeftChild(sibling);
                    }
                    else{
                        parent.getParent().setRightChild(sibling);
                    }
                }
                else {
                    sibling.setParent(null);
                    this.root = sibling;
                }
                parent.setParent(sibling);
                parent.setLeftChild(sibling.getRightChild());
                if (sibling.getRightChild() != null){
                    sibling.getRightChild().setParent(parent);
                }
                sibling.setRightChild(parent);
                sibling.setColor(INode.BLACK);
                parent.setColor(INode.RED);
                deleteBlack(node);
            }
        }
    }

    private INode<T, V> insertInto (INode<T, V> parent, T key, V value){
        INode<T, V> toInsert = new Node<>();
        toInsert.setParent(parent);
        toInsert.setKey(key);
        toInsert.setValue(value);
        toInsert.setColor(INode.RED);
        toInsert.setLeftChild(new Node<>());
        toInsert.setRightChild(new Node<>());
        if (parent.getKey().compareTo(key) > 0){
            parent.setLeftChild(toInsert);
        }
        else{
            parent.setRightChild(toInsert);
        }
        return toInsert;
    }

    private void recolor (INode<T, V> x){
        INode<T, V> parent = x.getParent();
        if (parent == null){
            x.setColor(INode.BLACK);
            return;
        }
        if (parent.getColor() == INode.RED){
            if (parent.getParent() != null && parent.getParent().getLeftChild() == parent && parent.getParent().getRightChild().getColor() == INode.RED) {
                uncleRED(parent.getParent());
            }
            else if (parent.getParent()!= null && parent.getParent().getRightChild() == parent && parent.getParent().getLeftChild().getColor() == INode.RED) {
                uncleRED(parent.getParent());
            }
            else if (parent.getParent()!= null && parent.getParent().getLeftChild() == parent && parent.getParent().getRightChild().getColor() == INode.BLACK) {
                uncleBLACK(x, parent, parent.getParent());
            }
            else if (parent.getParent()!= null && parent.getParent().getRightChild() == parent && parent.getParent().getLeftChild().getColor() == INode.BLACK) {
                uncleBLACK(x, parent, parent.getParent());
            }
        }
    }

    private void uncleRED (INode<T, V> gparent){
        gparent.setColor(INode.RED);
        gparent.getLeftChild().setColor(INode.BLACK);
        gparent.getRightChild().setColor(INode.BLACK);
        recolor(gparent);
    }

    private void uncleBLACK (INode<T, V> child, INode<T, V> parent, INode<T, V> gparent){
        if (gparent.getLeftChild() == parent){
            if (parent.getRightChild() == child){
                child.setParent(gparent);
                gparent.setLeftChild(child);
                parent.setParent(child);
                parent.setRightChild(child.getLeftChild());
                child.getLeftChild().setParent(parent);
                child.setLeftChild(parent);

                if (this.getRoot() != gparent){
                    child.setParent(gparent.getParent());
                    if (gparent.getParent().getLeftChild() == gparent){
                        gparent.getParent().setLeftChild(child);
                    }
                    else if (gparent.getParent().getRightChild() == gparent){
                        gparent.getParent().setRightChild(child);
                    }
                }
                else {
                    child.setParent(null);
                    this.root = child;
                }
                child.setLeftChild(parent);
                gparent.setLeftChild(child.getRightChild());
                child.getRightChild().setParent(gparent);
                child.setRightChild(gparent);
                gparent.setParent(child);
                boolean temp = child.getColor();
                child.setColor(gparent.getColor());
                gparent.setColor(temp);
            }
            else {
                if (this.getRoot() != gparent){
                    parent.setParent(gparent.getParent());
                    if (gparent.getParent().getLeftChild() == gparent){
                        gparent.getParent().setLeftChild(parent);
                    }
                    else if (gparent.getParent().getRightChild() == gparent){
                        gparent.getParent().setRightChild(parent);
                    }
                }
                else {
                    parent.setParent(null);
                    this.root = parent;
                }
                parent.setLeftChild(child);
                gparent.setLeftChild(parent.getRightChild());
                parent.getRightChild().setParent(gparent);
                parent.setRightChild(gparent);
                gparent.setParent(parent);
                boolean temp = parent.getColor();
                parent.setColor(gparent.getColor());
                gparent.setColor(temp);
            }
        }
        if (gparent.getRightChild() == parent){
            if (parent.getLeftChild() == child){
                child.setParent(gparent);
                gparent.setRightChild(child);
                parent.setParent(child);
                parent.setLeftChild(child.getRightChild());
                child.getRightChild().setParent(parent);
                child.setRightChild(parent);

                if (this.getRoot() != gparent){
                    child.setParent(gparent.getParent());
                    if (gparent.getParent().getLeftChild() == gparent){
                        gparent.getParent().setLeftChild(child);
                    }
                    else if (gparent.getParent().getRightChild() == gparent){
                        gparent.getParent().setRightChild(child);
                    }
                }
                else {
                    child.setParent(null);
                    this.root = child;
                }
                child.setRightChild(parent);
                gparent.setRightChild(child.getLeftChild());
                child.getLeftChild().setParent(gparent);
                child.setLeftChild(gparent);
                gparent.setParent(child);
                boolean temp = child.getColor();
                child.setColor(gparent.getColor());
                gparent.setColor(temp);
            }
            else{
                if (this.getRoot() != gparent){
                    parent.setParent(gparent.getParent());
                    if (gparent.getParent().getLeftChild() == gparent){
                        gparent.getParent().setLeftChild(parent);
                    }
                    else if (gparent.getParent().getRightChild() == gparent){
                        gparent.getParent().setRightChild(parent);
                    }
                }
                else {
                    parent.setParent(null);
                    this.root = parent;
                }
                parent.setRightChild(child);
                gparent.setRightChild(parent.getLeftChild());
                parent.getLeftChild().setParent(gparent);
                parent.setLeftChild(gparent);
                gparent.setParent(parent);
                boolean temp = parent.getColor();
                parent.setColor(gparent.getColor());
                gparent.setColor(temp);
            }
        }
    }
}