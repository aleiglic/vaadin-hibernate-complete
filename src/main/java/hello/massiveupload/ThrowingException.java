package hello.massiveupload;


import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.SucceededEvent;

@FunctionalInterface
public interface ThrowingException<T, E extends Exception> {
    void accept(SucceededEvent i) throws E;
    
    static <T, E extends Exception> ComponentEventListener<SucceededEvent> handlingExceptionWrapper(ThrowingException<T, E> throwingException,
			Class<E> exceptionClass) {

		return i -> {
			try {
				throwingException.accept(i);
			} catch (Exception ex) {
//				try {
//					E exCast = exceptionClass.cast(ex);
//					System.err.println("Exception occured : " + exCast.getMessage());
//				} catch (ClassCastException ccEx) {
//					throw new RuntimeException(ex);
//				}
				new Notification(ex.getMessage()).open();
				throw new RuntimeException(ex);
			}
		};
	}
}

