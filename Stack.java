import java.util.ArrayList;

public class Stack {
    public ArrayList<StackElement> stack;
    
    public Stack() {
        this.stack = new ArrayList<StackElement>();
    }

    public void push(StackElement newElement) {
        stack.add(newElement);
    }

    public StackElement pop() {
        int lastIndex = stack.size() - 1;
        StackElement removedItem = stack.get(lastIndex);
        stack.remove(lastIndex);

        return removedItem;
    }
}
