package event;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class EventManager<T extends EventMessage> {

	private ArrayList<EventListener> listeners;

	private Deque<T> eventStack = new ArrayDeque<T>();


	public EventManager() {
		listeners = new ArrayList<EventListener>();
		eventStack = new ArrayDeque<T>();
	}


	public void register(EventListener listener) {
		listeners.add(listener);
	}


	public void sendMessage(T message) {
		for (EventListener l : listeners) {
			l.message(message);
		}
	}


	public void pushMessage(T e) {
		eventStack.push(e);
	}


	public void sendStack() {
		while (eventStack.isEmpty() == false) {
			T e = eventStack.pop();
			sendMessage(e);
		}
	}
}
