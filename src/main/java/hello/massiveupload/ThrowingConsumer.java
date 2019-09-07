package hello.massiveupload;


import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.upload.SucceededEvent;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
    void accept(SucceededEvent i) throws E;
    
    static <T, E extends Exception> ComponentEventListener<SucceededEvent> handlingConsumerWrapper(ThrowingConsumer<T, E> throwingConsumer,
			Class<E> exceptionClass) {

		return i -> {
			try {
				throwingConsumer.accept(i);
			} catch (Exception ex) {
				try {
					E exCast = exceptionClass.cast(ex);
					System.err.println("Exception occured : " + exCast.getMessage());
				} catch (ClassCastException ccEx) {
					throw new RuntimeException(ex);
				}
			}
		};
	}
}

