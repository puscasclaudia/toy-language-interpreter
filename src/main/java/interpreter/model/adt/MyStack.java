package interpreter.model.adt;
import interpreter.exceptions.ADTException;
import java.util.ArrayDeque;
import java.util.Deque;

public class MyStack<T> implements IStack<T> {
    private final Deque<T> stack = new ArrayDeque<>();

    @Override
    public void push(T v) {
        stack.push(v);
    }

    @Override
    public T pop() throws ADTException {
        if (stack.isEmpty()) {
            throw new ADTException("Exception! Stack is empty");
        }
        return stack.pop();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public IStack<T> deepCopy() {
        MyStack<T> copy = new MyStack<>();
        Deque<T> temp = new ArrayDeque<>(stack);
        while (!temp.isEmpty()) {
            copy.stack.addLast(temp.removeLast());
        }
        return copy;
    }

    @Override
    public String toString() {
        return stack.toString();
    }
}
