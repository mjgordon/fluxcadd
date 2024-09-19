package event;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class EventManager {

	protected ArrayList<EventListener> listeners;

	private Deque<EventMessage> eventStack = new ArrayDeque<EventMessage>();


	public EventManager() {
		listeners = new ArrayList<EventListener>();
		eventStack = new ArrayDeque<EventMessage>();
	}


	public void register(EventListener listener) {
		listeners.add(listener);
	}


	public void sendMessage(EventMessage message) {
		for (EventListener l : listeners) {
			l.message(message);
		}
	}


	public void pushMessage(EventMessage e) {
		eventStack.push(e);
	}


	public void sendStack() {
		while (eventStack.isEmpty() == false) {
			EventMessage e = eventStack.pop();
			sendMessage(e);
		}
	}
}
