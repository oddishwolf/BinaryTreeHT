import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Attention: comparable supported but comparator is not
@SuppressWarnings("WeakerAccess")
public class BinaryTree<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T> {

    private static class Node<T> {
        final T value;

        Node<T> left = null;

        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;

    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        } else {
            assert closest.right == null;
            closest.right = newNode;
        }
        size++;
        return true;
    }

    boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    @Override
    public boolean remove(Object o) {
        Node<T> parent = root;
        Node<T> required;

        @SuppressWarnings("unchecked")
        T t = (T) o;

        if (root == null) return false;
        if (parent.value == t) {
            required = parent;

            //если вершина является удаляемым элементом (рассматриваем 3 ситуации)
            if (required.left == null && required.right == null) root = null;
            if (required.right == null) root = required.left;
            if (required.left == null) root = required.right;

            //если у вершины 2 поддерева
            else findTarget(required, parent, t);

        } else {

            //находим нужный корень с удаляемым узлом
            while ((parent.value.compareTo(t) > 0 && parent.left.value != t) || (parent.value.compareTo(t) < 0 && parent.right.value != t)) {
                if (parent.value.compareTo(t) < 0 && parent.right != o) parent = parent.right;
                else if (parent.value.compareTo(t) > 0 && parent.left != o) parent = parent.left;
                else return false;
            }

            //обозначаем удаляемый узел (аналогично рассматриваем 3 ситуации)
            if (parent.left != null && parent.left.value == t) required = parent.left;
            else if (parent.right != null && parent.right.value == t) required = parent.right;
            else return false;

            if (required.left == null && required.right == null) { //если нет дочерних узлов
                if (parent.left == required) parent.left = null;
                else if (parent.right == required) parent.right = null;
            }

            //если один дочерний узел
            if ((required.left != null && required.right == null) || (required.left == null && required.right != null)) {
                if (parent.left == required && required.left != null) parent.left = required.left;
                if (parent.right == required && required.right != null) parent.right = required.right;
                if (parent.left == required && required.right != null) parent.left = required.right;
                if (parent.right == required && required.left != null) parent.right = required.left;
            }

            //если 2 дочерних узла
            if (required.left != null && required.right != null) findTarget(required, parent, t);

        }
        size--;
        return true;
    }

    public void findTarget(Node<T> object, Node<T> parent, T t) {
        Node<T> target;

        if ((object.right != null ? object.right.left : null) != null) {
            target = object.right;
            while (target.left.left != null) target = target.left;
        } else target = null;

        if (parent.value == t) {
            if (target != null) {
                Node<T> minimal = target.left;
                target.left = minimal.right;
                minimal.right = object.right;
                minimal.left = object.left;
                root = minimal;
            } else {
                if (object.right != null) {
                    object.right.left = object.left;
                }
                root = object.right;
            }
        } else if (object.left != null && object.right != null) {
            if (target != null) {
                Node<T> minimal = target.left;
                target.left = minimal.right;
                minimal.left = object.left;
                minimal.right = object.right;
                if (parent.left == object) parent.left = minimal;
                else if (parent.right == object) parent.right = minimal;
            } else {
                object.right.left = object.left;
                if (parent.left == object) parent.left = object.right;
                else if (parent.right == object) parent.right = object.right;
            }
        }
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    public class BinaryTreeIterator implements Iterator<T> {

        private Node<T> current = null;

        BinaryTreeIterator() {
        }

        private Node<T> findNext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return findNext() != null;
        }

        @Override
        public T next() {
            current = findNext();
            if (current == null) throw new NoSuchElementException();
            return current.value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    @Override
    public int size() {
        return size;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
}
