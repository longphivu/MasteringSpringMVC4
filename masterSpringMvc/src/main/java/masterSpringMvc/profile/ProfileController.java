package masterSpringMvc.profile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import masterSpringMvc.config.PicturesUploadProperties;
import masterSpringMvc.date.USLocalDateFormatter;

@Controller
public class ProfileController {

	private final Resource picturesDir;
	private final Resource anonymousPicture;
	private final MessageSource messageSource;
	private final UserProfileSession userProfileSession;

	@Autowired
	public ProfileController(UserProfileSession userProfileSession, 
							PicturesUploadProperties uploadProperties, 
							MessageSource messageSource) {
		picturesDir = uploadProperties.getUploadPath();
		anonymousPicture = uploadProperties.getAnonymousPicture();
		this.userProfileSession = userProfileSession;
		this.messageSource = messageSource;
	}

	@ModelAttribute
	public ProfileForm getProfileForm() {
		return userProfileSession.toForm();
	}

	@ModelAttribute("dateFormat")
	public String localeFormat(Locale locale) {
		return USLocalDateFormatter.getPattern(locale);
	}

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String displayProfile(ProfileForm profileForm) {
		return "/profile/profilePage";
	}

	@RequestMapping(value = "/profile", params = { "save" }, method = RequestMethod.POST)
	public String saveProfile(@Valid ProfileForm profileForm, BindingResult binding) {

		if (binding.hasErrors()) {
			return "/profile/profilePage";
		}
		userProfileSession.saveForm(profileForm);
		System.out.println("ok " + profileForm);
		return "redirect:/search/mixed;keywords=" + String.join(",", profileForm.getTastes());
	}

	@RequestMapping(value = "/profile", params = { "addTaste" }, method = RequestMethod.POST)
	public String addRow(ProfileForm profileForm, BindingResult binding) {
		profileForm.getTastes().add(null);
		return "profile/profilePage";
	}

	@RequestMapping(value = "/profile", params = { "removeTaste" }, method = RequestMethod.POST)
	public String removeRow(ProfileForm profileForm, HttpServletRequest req) {
		Integer rowId = Integer.valueOf(req.getParameter("removeTaste"));
		profileForm.getTastes().remove(rowId.intValue());
		return "profile/profilePage";
	}
	
	@RequestMapping(value = "/profile", params = { "upload" }, method = RequestMethod.POST)
	public String upload(MultipartFile file, Model model, Locale locale) throws IOException {
		if (file == null || !isImage(file)) {
			model.addAttribute("error", messageSource.getMessage("upload.io.fileType", null, locale));
			return "profile/profilePage";
			//throw new IOException ("Upload file error!");
		}
		
		Resource picturePath = copyFileToPictures(file);
		userProfileSession.setPicturePath(picturePath);
		
		return "profile/profilePage";
	}
	
	@RequestMapping(value = "/uploadedPicture")
	public void getUploadedPicture(HttpServletResponse response) throws IOException {
		Resource picturePath = userProfileSession.getPicturePath();
		if (picturePath == null) {
			picturePath = anonymousPicture;
		}
		response.setHeader("Content-Type", URLConnection.guessContentTypeFromName(picturePath.toString()));
		IOUtils.copy(picturePath.getInputStream(), response.getOutputStream());
		//Files.copy(picturePath, response.getOutputStream());
	}
	
	@ExceptionHandler(IOException.class)
	public String handleIOException(IOException e, RedirectAttributes redirectAttrs) {
		redirectAttrs.addAttribute("error", e.getMessage());
		return "redirect:profile";
		
	}
	
	private Resource copyFileToPictures(MultipartFile file) throws IOException {
		String fileType = getFileExtension(file.getOriginalFilename());
		File tempFile = File.createTempFile("pic", fileType, picturesDir.getFile());
		try (
				InputStream in = file.getInputStream();
				OutputStream out = new FileOutputStream(tempFile);){
			IOUtils.copy(in, out);
		}
		return new FileSystemResource(tempFile);
	}
	
	private boolean isImage(MultipartFile file) {
		String fileType = file.getContentType();
		return fileType.startsWith("image");
	}
	
	private String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.'));
	}

}
