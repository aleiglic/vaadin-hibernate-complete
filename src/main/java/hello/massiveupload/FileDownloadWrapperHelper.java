package hello.massiveupload;

import java.io.IOException;
import java.io.InputStream;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.server.StreamResource;

import org.vaadin.olli.FileDownloadWrapper;

public class FileDownloadWrapperHelper {
    
    public static FileDownloadWrapper getWrapper(InputStream is, String btnLabel, String fileName) throws IOException {
		FileDownloadWrapper buttonWrapper = null;
		Button button = new Button(btnLabel);
		if(is != null) 
			buttonWrapper = new FileDownloadWrapper(new StreamResource(fileName, () -> is));
		else
			throw new IOException("The file input was not correctly opened");
		button.setWidth("200px");
		buttonWrapper.wrapComponent(button);
		return buttonWrapper;
	}
    
}