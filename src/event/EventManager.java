package event;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public abstract class EventManager {
	public EventManager() {
		listeners = new ArrayList<EventListener>();
		eventStack = new ArrayDeque<EventMessage>();
	}

	private ArrayList<EventListener> listeners;

	protected Deque<EventMessage> eventStack = new ArrayDeque<EventMessage>();

	public void register(EventListener listener) {
		listeners.add(listener);
		System.out.println(listener.getClass());
	}

	public void pushMessage(EventMessage e) {
		eventStack.push(e);
	}
	
	public void sendMessage(EventMessage message) {
		for (EventListener l : listeners)
			l.message(message);
	}
	
	public void sendStack() {
		while(eventStack.isEmpty() == false) {
			EventMessage e = eventStack.pop();
			sendMessage(e);
		}
	}
}
