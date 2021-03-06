package masterSpringMvc.profile;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserProfileSession implements Serializable {
	private String twitterHandle;
	private String email;
	private LocalDate birthDate;
	private List<String> tastes = new ArrayList<>();
	private Resource PicturePath;
	
	public Resource getPicturePath() {
		return PicturePath;
	}
	public void setPicturePath(Resource picturePath) {
		PicturePath = picturePath;
	}
	
	public void saveForm(ProfileForm profileForm) {
		this.twitterHandle = profileForm.getTwitterHandle();
		this.email = profileForm.getEmail();
		this.birthDate = profileForm.getBirthDate();
		this.tastes = profileForm.getTastes();
	}

	public ProfileForm toForm() {
		ProfileForm profileForm = new ProfileForm();
		profileForm.setTwitterHandle(twitterHandle);
		profileForm.setEmail(email);
		profileForm.setBirthDate(birthDate);
		profileForm.setTastes(tastes);
		return profileForm;
	}
}
