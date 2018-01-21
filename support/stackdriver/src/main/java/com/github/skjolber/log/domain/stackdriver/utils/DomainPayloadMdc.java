package com.github.skjolber.log.domain.stackdriver.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class DomainPayloadMdc<T extends DomainPayload> {

	private static volatile List<DomainPayloadMdc<? extends DomainPayload>> mdcs = new ArrayList<>();

	public static void register(DomainPayloadMdc<? extends DomainPayload> mdc) {
		// defensive copy operation
		List<DomainPayloadMdc<? extends DomainPayload>> next = new ArrayList<>(DomainPayloadMdc.mdcs);
		
		next.add(mdc);
		
		DomainPayloadMdc.mdcs = next;
	}

	public static void unregister(DomainPayloadMdc<? extends DomainPayload> mdc) {
		// defensive copy operation
		List<DomainPayloadMdc<? extends DomainPayload>> next = new ArrayList<>(DomainPayloadMdc.mdcs);

		next.remove(mdc);

		DomainPayloadMdc.mdcs = next;
	}

	public static List<DomainPayloadMdc<? extends DomainPayload>> getMdcs() {
		return mdcs;
	}

	public static DomainPayloadMdc<? extends DomainPayload> getMdc(String qualifier) {
		List<DomainPayloadMdc<? extends DomainPayload>> mdcs = getMdcs();
		for (DomainPayloadMdc<? extends DomainPayload> domainMdc : mdcs) {
			if(Objects.equals(domainMdc.getQualifier(), qualifier)) {
				return domainMdc;
			}
		}
		return null;
	}

	public static <T extends DomainPayload> DomainPayloadMdc<? extends DomainPayload> mdcForType(Class<T> type) {
		List<DomainPayloadMdc<? extends DomainPayload>> mdcs = getMdcs();
		for (DomainPayloadMdc<? extends DomainPayload> domainMdc : mdcs) {
			if(domainMdc.supports(type)) {
				return domainMdc;
			}
		}
		return null;
	}

	public static void removeAll() {
		List<DomainPayloadMdc<? extends DomainPayload>> mdcs = getMdcs();
		for (DomainPayloadMdc<? extends DomainPayload> domainMdc : mdcs) {
			domainMdc.remove();
		}
	}
	
	public static DomainPayload mdc(DomainPayload marker) {
		DomainPayload domainMarker = (DomainPayload)marker;
		domainMarker.pushContext();
		
		if(marker.hasReferences()) {
			for(DomainPayload reference : marker.getRefereces()) {
				
				// a mdc payload can be a child of multiple markers
				// but not have any children itself
				domainMarker.remove(reference);

				mdc(reference);
			}
		}
		return domainMarker;
	}
	
	protected ThreadLocal<T> inheritableThreadLocal = new InheritableThreadLocal<T>() {
		
		@Override
		protected T childValue(T parentValue) {
            if (parentValue == null) {
                return null;
            }
            return parentValue;
		}
    };

	protected String qualifier;

	public DomainPayloadMdc(String qualifier) {
		this.qualifier = qualifier;
	}
	
	public String getQualifier() {
		return qualifier;
	}

	public T get() {
		return inheritableThreadLocal.get();
	}
    
    public void push(T child) {
		inheritableThreadLocal.set(child);
    }

	public void pop() {
    	T tail = inheritableThreadLocal.get();
    	if(tail == null) {
    		throw new IllegalArgumentException("Cannot pop MDC stack, already empty");
    	}
    	
		@SuppressWarnings("unchecked")
		T parent = (T) tail.getParent();
		if(parent != null) {
			inheritableThreadLocal.set(parent);
		} else {
	    	inheritableThreadLocal.remove();
		}
	}

    @SuppressWarnings("unchecked")
	public void pop(T item) {
    	T tail = inheritableThreadLocal.get();
    	if(tail == null) {
    		throw new IllegalArgumentException("Cannot pop MDC stack, already empty");
    	} else if(item != tail) {
    		throw new IllegalArgumentException("MDC entries must be removed in the reverse order as they were added");
    	}
    	
		T parent = (T) item.getParent();
		if(parent != null) {
			inheritableThreadLocal.set(parent);
		} else {
	    	inheritableThreadLocal.remove();
		}
    }
    
    /**
     * Check whether a marker is in the MDC stack.
     * 
     * @param marker the marker to check
     * @return true if present in the stack
     */
    
    public boolean exists(DomainPayload marker) {
    	DomainPayload tail = inheritableThreadLocal.get();
    	
    	if(tail != null) {
    		do {
    			if(tail == marker) {
    				return true;
    			}
    			tail = tail.parent;
    		} while(tail != null);
    	}
    	
    	return false;
    }
    public void remove() {
    	inheritableThreadLocal.remove();
    }

	
	public void register() {
		DomainPayloadMdc.register(this);
	}
	
	public void unregister() {
		DomainPayloadMdc.unregister(this);
	}
	
	public abstract T createPayload();
	
	public abstract boolean supports(Class<? extends DomainPayload> type);

	public abstract Class<T> getType();

}
