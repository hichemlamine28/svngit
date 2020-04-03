package com.arkeup.link_innov.gestion_profil_mcs.infrastructure.profil;

import java.util.List;

import javax.validation.Valid;

import com.arkeup.link_innov.gestion_profil_mcs.donnee.dto.*;
import com.arkeup.link_innov.gestion_profil_mcs.service.applicatif.cud.contact_consultation.ContactConsultationCUDSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import com.arkeup.link_innov.gestion_profil_mcs.contrainte.errors.ValidationException;
import com.arkeup.link_innov.gestion_profil_mcs.contrainte.validator.ProfilDTOValidator;
import com.arkeup.link_innov.gestion_profil_mcs.donnee.dto.commun.CustomPageDTO;
import com.arkeup.link_innov.gestion_profil_mcs.infrastructure.utils.PermissionsAndStatusUtils;
import com.arkeup.link_innov.gestion_profil_mcs.service.applicatif.cud.profil.ProfilCUDSA;
import com.arkeup.link_innov.gestion_profil_mcs.service.applicatif.read.information.UserInformationRSAImpl;
import com.arkeup.link_innov.gestion_profil_mcs.service.applicatif.read.profil.ProfilRSA;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 *
 * @author bona
 */
@RestController
@RequestMapping(value = "/profil")
public class ProfilController {

	@Autowired
	private ProfilCUDSA profilCUDSA;

	@Autowired
	private ProfilRSA profilRSA;

	@Autowired
	private ContactConsultationCUDSA contactConsultationCUDSA;

	@Autowired
	UserInformationRSAImpl userInformationRSAImpl;

	@Autowired
	private ProfilDTOValidator profilDTOValidator;

	@InitBinder("profilDTO")
	protected void initProfilDTOBinder(WebDataBinder binder) {
		binder.setValidator(profilDTOValidator);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Update user information", notes = "This WS is used to Update user information.")
	@PutMapping(path = "/update")
	public ProfilDTO updateProfil(
			@ApiParam(name = "ProfilDTO", value = "{\"id\":\"uuid\",\"firstname\":\"firstName\",\"lastname\":\"lastName\",	\"mediaDTO\":{\"id\":\"mediaUuid\"}}", required = true) @Valid @RequestBody ProfilDTO profilDTO,
			Errors errors) {
//		if (errors.hasErrors()) {
//			throw new ValidationException(errors);
//		}
		return profilCUDSA.update(profilDTO);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "get user auth information", notes = "This WS is used to get user auth information.")
	@GetMapping(value = { "/getAuthInformation" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ProfilDTO getAuthInformation() {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = user.getUsername();
		return profilRSA.getProfil(userName);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "get user information", notes = "This WS is used to get user information.")
	@GetMapping(value = { "/getInformation" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ProfilDTO getProfil() {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = user.getUsername();
		return profilRSA.getProfil(userName);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "get user information", notes = "This WS is used to get user information.")
	@GetMapping(value = { "/getInformation/{username}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ProfilDTO getProfilByusername(@PathVariable("username") String userName) {
		return profilRSA.getProfilByUserName(userName);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "get user information by its pseudo", notes = "This WS is used to get user information by providing its pseudo")
	@GetMapping(value = { "/getInformation/pseudo/{pseudoName}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ProfilDTO getProfilByPseudoName(@PathVariable("pseudoName") String pseudoName) {
		return profilRSA.getProfilByPseudoName(pseudoName);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Generate media token for user picture", notes = "This WS is used to generate media token for user picture.")
	@PostMapping(path = "/picture/token", produces = { MediaType.APPLICATION_JSON_VALUE })
	public MediaDTO generatePictureProfilToken() {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return profilCUDSA.generatePictureToken(user.getUsername());
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Verify permission to modify user profil", notes = "This WS is used to verify permission to modify user profil.")
	@GetMapping(path = "/permission/check", params = { "profilId" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ProfilAdminDTO verifyPermission(
			@ApiParam(value = "{\"profilId\":\"profilId\"}", required = true) @RequestParam(value = "profilId", defaultValue = "uuid") String profilId) {
		return profilRSA.verifyPermission(profilId);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Delete user picture", notes = "This WS is used to delete user picture.")
	@DeleteMapping(path = "/delete/picture", params = { "profilId", "mediaId" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ProfilDTO deletePictureProfil(
			@ApiParam(value = "{\"profilId\":\"profilId\"}", required = true) @RequestParam(value = "profilId", defaultValue = "uuid") String profilId,
			@ApiParam(value = "{\"mediaId\":\"mediaId\"}", required = true) @RequestParam(value = "mediaId", defaultValue = "uuid") String mediaId) {
		return profilCUDSA.deletePicture(profilId, mediaId);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Delete background profil", notes = "This WS is used to delete the background profil.")
	@DeleteMapping(path = "/delete/background/{profilId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ProfilDTO deleteBackgroundProfil(@PathVariable("profilId") String profilId) {
		return profilCUDSA.deleteBackground(profilId);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Update user picture information in profil", notes = "This WS is used to Update user picture information in profil.")
	@PutMapping(path = "/update/picture")
	public ProfilDTO updatePictureInformationInProfil(
			@ApiParam(name = "mediaDTO", value = "{\"id\":\"mediaId\",\"defaultUrl\":\"https://url.image\",\"type\":\"PICTURE\"}", required = true) @RequestBody MediaDTO mediaDTO) {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return profilCUDSA.updatePictureInProfil(user.getUsername(), mediaDTO);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Update user background picture information in profil", notes = "This WS is used to Update user background picture information in profil.")
	@PutMapping(path = "/update/background")
	public ProfilDTO updateBackgroundPictureInformationInProfil(
			@ApiParam(name = "mediaDTO", value = "{\"id\":\"mediaId\",\"defaultUrl\":\"https://url.image\",\"type\":\"PICTURE\"}", required = true) @RequestBody MediaDTO mediaDTO) {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return profilCUDSA.updateBackgroundPictureInProfil(user.getUsername(), mediaDTO);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Check user permission", notes = "This WS is used to check user permission.")
	@GetMapping(path = "/permission", params = { "profilId" })
	public ProfilAdminDTO isOwner(
			@ApiParam(value = "{\"profilId\":\"profilId\"}", required = true) @RequestParam(value = "profilId", defaultValue = "uuid") String profilId) {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return profilRSA.isOwner(user.getUsername(), profilId);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Get users information by their id", notes = "This WS is used to get users informations.")
	@PostMapping(path = "/getInformationByIds", produces = { MediaType.APPLICATION_JSON_VALUE })
	public PageContactsDTO getUsersInformationByIds(@RequestBody List<String> userIds,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "filter", required = false) String filter, Pageable pageable) {

		return profilRSA.getProfilByIds(userIds, type, filter, pageable);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Update user corporation", notes = "This WS is used to Update user corporation.")
	@PutMapping(path = "/updateUserCorporation")
	public ProfilDTO updateUserCorporation(
			@RequestParam(value = "corporationId", defaultValue = "uuid") String corporationId) {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = user.getUsername();
		return profilCUDSA.updateUserCorporation(userName, corporationId);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Get users profils information by their id", notes = "This WS is used to get users profils information by their id.")
	@PostMapping(path = "/getProfilsByIds", produces = { MediaType.APPLICATION_JSON_VALUE })
	public List<ProfilDTO> getProfilsInformationsByIds(@RequestBody List<String> userIds) {
		return profilRSA.getListProfilByIds(userIds);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Get paginated user's profils information by their id", notes = "This WS is used to get paginated user's profils information by their id.")
	@PostMapping(path = "/get/users", produces = { MediaType.APPLICATION_JSON_VALUE })
	public CustomPageDTO<ProfilDTO> getPaginatedProfilsInformationsByIds(@RequestBody List<String> userIds,
			Pageable pageable) {
		return profilRSA.getPaginatedProfilByIds(userIds, pageable);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Check if profile is initialised", notes = "This WS is used to check if profile is initialised.")
	@GetMapping(path = "/isProfileInitialised")
	public ProfilDTO checkisProfileInitialised() {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return profilRSA.checkisProfileInitialised(user.getUsername());
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Update user profil InitialisedStatus", notes = "This WS is used to Update user initialisedStatus profil.")
	@PutMapping(path = "/updateProfileInitialisedStatus")
	public ProfilDTO updateProfileInitialisedStatus(
			@RequestParam(value = "initialisedStatus") Boolean initialisedStatus) {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = user.getUsername();
		return profilCUDSA.updateProfileInitialisedStatus(userName, initialisedStatus);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "get user information", notes = "This WS is used to get user information.")
	@GetMapping(value = { "/userValidation/{userId}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public UserValidatorDTO getInformation(@PathVariable("userId") String userId) {
		return userInformationRSAImpl.userValidation(userId);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "share user profil", notes = "This WS is used to share user profil.")
	@PutMapping(path = "/shareProfil/{userId}")
	public ProfilDTO shareProfil(@PathVariable("userId") String userId, @RequestBody PostDTO postDTO) {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = user.getUsername();
		return profilCUDSA.share(userName, userId, postDTO);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "share user profil", notes = "This WS is used to share user profil.")
	@PutMapping(path = "/shareProfil/{userId}/group/{groupId}")
	public ProfilDTO shareProfilToGroup(@PathVariable("userId") String userId, @RequestBody PostDTO postDTO,
			@PathVariable("groupId") String groupId) {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = user.getUsername();
		return profilCUDSA.share(userName, userId, postDTO);
	}

	@ApiOperation(value = "get public profil information", notes = "This WS is used to get public profil information.")
	@GetMapping(value = { "/information/public/{user_id}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public PublicProfilDTO getPublicProfil(@PathVariable("user_id") String userId) {
		return profilRSA.getPublicProfil(userId);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Update user phone number", notes = "This WS is used to Update user phone number.")
	@PutMapping(path = "/phoneNumber/update")
	public ProfilDTO updatePhoneNumber(
			@ApiParam(name = "ProfilDTO", value = "{\"phoneNumber\":\"phoneNumber\"}", required = true) @RequestBody ProfilDTO profilDTO) {
		return profilCUDSA.updatePhoneNumber(profilDTO.getPhoneNumber());
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "get contact picture", notes = "This WS is used to get get contact picture.")
	@GetMapping(value = { "/contact/picture/{user_id}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public MediaDTO getContactPicture(@PathVariable("user_id") String userId) {
		return profilRSA.getContactPicture(userId);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Export profil data to CSV", notes = "This WS is used to export profil data to CSV.")
	@GetMapping(path = "/export")
	public DeferredResult<ProfilDTO> exportProfilToCSV(@RequestHeader("Authorization") String token) {
		String userName = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getUsername();
		DeferredResult<ProfilDTO> output = profilRSA.exportProfilToCSV(token, userName);
		return output;
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Get Export file URL", notes = "This WS is used to get Export file Get Export file URL.")
	@GetMapping(path = "/export/url")
	public String getExportFileUrl() {
		String userName = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getUsername();
		return profilRSA.getExportFileUrl(userName);
	}
	
	@ApiOperation(value = "get new subscribed users informations", notes = "This WS is used to get new subscribed users informations.")
	@GetMapping(value = { "/last/subscribed/users" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public CustomPageDTO<ProfilDTO> getNewSubscribedUsers(Pageable pageable) {
		return profilRSA.getNewSubscribedUsers(pageable);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "update contact consultation", notes = "This WS is used to update contact consultation information by profil.")
	@GetMapping(value = { "/contact/consultation" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ProfilDTO updateContactConsultation() {
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = user.getUsername();
		return contactConsultationCUDSA.updateContactConsultation(userName);
	}

	@PreAuthorize(PermissionsAndStatusUtils.ROLEUSER)
	@ApiOperation(value = "Update profil to has media", notes = "This WS is used to update profil to has media.")
	@PostMapping(path = "/setProfilHasMedia")
	public ProfilDTO setProfilHasMedia(
			@ApiParam(name = "ProfilHasMediaDTO", value = "{\"profilId\":\"uuid\", \"hasMedia\":\"true\", \"type\":\"MEDIA\"}", required = true) @RequestBody ProfilHasMediaDTO profilHasMediaDTO) {
		return profilCUDSA.setProfilHasMedia(profilHasMediaDTO);
	}

	@ApiOperation(value = "Update profil to has media", notes = "This WS is used to update profil to has media.")
	@PostMapping(path = "/setProfilHasMedia/anonymous")
	public ProfilDTO setProfilHasMediaAnonymous(
			@ApiParam(name = "ProfilHasMediaDTO", value = "{\"profilId\":\"uuid\", \"hasMedia\":\"true\", \"type\":\"MEDIA\"}", required = true) @RequestBody ProfilHasMediaDTO profilHasMediaDTO) {
		return profilCUDSA.setProfilHasMedia(profilHasMediaDTO);
	}

	@ApiOperation(value = "update profil has Media", notes = "WS update profil has Media")
	@PreAuthorize(PermissionsAndStatusUtils.ROLEADMIN)
	@PutMapping(value = {"/update_all_has_media/{type}"})
	@ResponseBody
	public IsHasMediaUpdatedDTO updateAllHasMedia(@PathVariable("type") String type) {
		return this.profilCUDSA.updateAllHasMedia(type);
	}

}
