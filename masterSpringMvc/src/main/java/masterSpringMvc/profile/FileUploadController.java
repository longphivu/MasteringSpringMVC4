package masterSpringMvc.profile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileUploadController {
	
	public static final Resource PICTURES_DIR = new	FileSystemResource("./pictures");

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String displayUploadFile() {
		return "profile/uploadPage";
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String onUpload(MultipartFile file, RedirectAttributes redirectAttrs) throws IOException {
		
		if (file == null || !isImage(file)) {
			redirectAttrs.addFlashAttribute("error", "Incorrect file. Please upload a picture.");
			throw new IOException("error !!");
			//return "redirect:/upload";
		}
		
		String fileName = file.getOriginalFilename();
		File tempFile = File.createTempFile("pic", getFileExtension(fileName), PICTURES_DIR.getFile());
		
		try (InputStream in = file.getInputStream(); 
				OutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}
		
		return "profile/uploadPage";
	}
	
	@RequestMapping(value = "/uploadedPicture")
	public void getUploadedImage(HttpServletResponse response) throws IOException {
		ClassPathResource resource = new ClassPathResource("/images/anonymous.png");
		response.setHeader("Content-Type", URLConnection.guessContentTypeFromName(resource.getFilename()));
		IOUtils.copy(resource.getInputStream(), response.getOutputStream());
	}
	
	@ExceptionHandler(IOException.class)
	public ModelAndView handleIOException(IOException e) {
		ModelAndView modelAndView = new ModelAndView("profile/uploadPage");
		modelAndView.getModel().put("error", e.getMessage());
		return modelAndView;
		
	}
	
	private String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.'));
	}
	
	private boolean isImage(MultipartFile file) {
		return file.getContentType().startsWith("image");
	}
}
