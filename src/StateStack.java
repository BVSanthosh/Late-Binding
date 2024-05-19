import java.util.ArrayList;

public class StateStack {
    
    private ArrayList<State> list = new ArrayList<>();   //the stack data structure 

    //adds a state to the list
    public void push(State state){
        list.add(state);
    }

    //gets the top state from the stack and then removes it
    public State pop(){

        if (list.isEmpty()) {
			return null;
		}

        State topState = list.get(list.size() - 1);;
        list.remove(list.size() - 1);

        return topState;
    }

    //gets the top state from the stack without removing it 
    public State peek(){

        if (!list.isEmpty()) {
            return list.get(list.size() - 1);
		}
        else{
            return null;
        }
    }

    //gets the size of the stack
    public int size() {
		return list.size();
	}

    //checks whether the stack is empty
    public boolean isEmpty() {
		return list.isEmpty();
	}

    //gets a specified state from the stack
    public State getState(int num){
        return list.get(num);
    }
}