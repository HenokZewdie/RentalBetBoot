package DebalFelagiPackage;

import com.cloudinary.utils.ObjectUtils;
import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.util.*;

//For Email Service


@Controller
public class HomeController {

    @Autowired
    private UserValidator userValidator;
    @Autowired
    private HouseValidator houseValidator;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private CloudinaryConfig cloudc;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    String Sessionfullname, sessionUsername, fromSession, toSession, messageSession, stateSession;

    @RequestMapping("/")
    public String index(Model model, House house, IpAddress ipAddress)
    {
        model.addAttribute("house",new House());
        model.addAttribute("ipadd", new IpAddress());
        return "home";
    }
    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping(value = "/ipfinder", method = RequestMethod.GET)
    public String finderGet(Model model){
        model.addAttribute("ipadd", new IpAddress());
        return "/ipfinder";
    }

    @RequestMapping(value = "/ipfinder", method = RequestMethod.POST)
    public String finderPost(@ModelAttribute IpAddress ipAdd, Model model){
        ipAdd.processIP(ipAdd.getIp());
        System.out.println(ipAdd.getcName());
        return "redirect:/";
    }

    @RequestMapping(value="/register", method = RequestMethod.GET)
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "register";
    }
    @RequestMapping(value="/register", method = RequestMethod.POST)
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        userValidator.validate(user, result);
        if (result.hasErrors()) {
            return "register";
        } else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Successfully Created");
        }
        user = userRepository.findByUsername(user.getUsername());
        model.addAttribute("register1", user);
        return "/login";
    }

    @RequestMapping(value = "/houseregister", method = RequestMethod.GET)
    public String houseGet(Model model){
        model.addAttribute("house", new House());
        return "houseregister";
    }

    @RequestMapping(value = "/houseregister", method = RequestMethod.POST)
    public String housePOST(@RequestParam("file") MultipartFile file, House house, RedirectAttributes redirectAttributes,
                            Model model,  Principal principal, BindingResult result){

        if (file.isEmpty()){
            redirectAttributes.addFlashAttribute("message","Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {
            Map uploadResult =  cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));

            model.addAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            String filename = uploadResult.get("public_id").toString() + "." + uploadResult.get("format").toString();
            //String effect = p.getTitle();
            house.setPhoto("<img src='http://res.cloudinary.com/henokzewdie/image/upload/" +filename+"' width='200px'/>");
            house.setDetailphoto("<img src='http://res.cloudinary.com/henokzewdie/image/upload/" +filename+"' width='500px'/>");

            //System.out.printf("%s\n", cloudc.createUrl(filename,900,900, "fit"));

        } catch (IOException e){
            e.printStackTrace();
            model.addAttribute("message", "Sorry I can't upload that!");
        }

        house.setDate(new Date());
        house.setUsername(principal.getName());
        houseRepository.save(house);
        model.addAttribute(new House());
         /*email a notification for everybody when the new house is registered using a forloop*/
        Iterable<Notification> notify= notificationRepository.findAll();
        for(Notification itrNotify: notify){
            String message = "Hello Ekele, New house has posted with the link below." ; //we will add the actual URL for the new house
            try {
                sendEmailWithoutTemplating(sessionUsername, fromSession, itrNotify.getEmail(), message);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/loginSuccess";
    }

    @RequestMapping(value = "/loginSuccess", method = RequestMethod.GET)
    public String displayTemp(Model model, Principal principal, User user, Notification notification){
        model.addAttribute("house", new House());
        model.addAttribute("notify", new Notification());
        user = userRepository.findByUsername(principal.getName());
        long min = user.getZipCode() - 50;
        long max = user.getZipCode() + 50;
        Set<House> zipList = new HashSet<>();
        for (long i = min; i<=max; i++){
            List<House> houseList = houseRepository.findByZipCode(i);
            zipList.addAll(houseList);
        }

        model.addAttribute("houseList", zipList);
        return "/displaytemplate";
    }

    @RequestMapping(value = "/searchstate", method = RequestMethod.GET)
    public String searchstateGet(Model model){
        model.addAttribute("house", new House());
        return "/searchstate";
    }
    @RequestMapping(value = "/searchstate", method = RequestMethod.POST)
    public String searchstate(Model model,  House house) throws Exception{
        model.addAttribute("messagesend", new MessageSend());
        model.addAttribute("notify", new Notification());
        stateSession = house.getState();
        List<House> zipList = houseRepository.findByState(stateSession);
        if(zipList.isEmpty()) {
            zipList = houseRepository.findByCity(stateSession);
            if (zipList.isEmpty()) {
                try{
                    long zipTemp = Long.parseLong(stateSession);
                    zipList = houseRepository.findByZipCode(zipTemp);
                }catch (Exception e){
                    return "redirect:/";
                }
            }
        }

          model.addAttribute("houseList", zipList);
        return "/displaytemplate";
    }

   /* @RequestMapping(value = "/searchstate", method = RequestMethod.POST)
    public HouseResponse searchstate(Model model,  House house){
        HouseResponse houseresp = new HouseResponse();

        model.addAttribute("messagesend", new MessageSend());
        model.addAttribute("notify", new Notification());
        stateSession = house.getState();
        List<House> zipList = houseRepository.findByState(stateSession);
        if(zipList.isEmpty()){
            zipList = houseRepository.findByCity(stateSession);
            if(zipList.isEmpty()){
                zipList = houseRepository.findByZipCode(Long.parseLong(house.getState()));
            }
        }
        model.addAttribute("houseList", zipList);

        houseresp.add(zipList);
        return houseresp;
    }*/

    @RequestMapping(value = "/searchzip", method = RequestMethod.GET)
    public String searchzipGet(Model model){
        model.addAttribute("house", new House());
        return "/searchzip";
    }
    @RequestMapping(value = "/searchzip", method = RequestMethod.POST)
    public String searchzip(Model model,  House house){
        model.addAttribute("house", new House());
        long min = house.getZipCode() - 10;
        long max = house.getZipCode() + 10;
        List<House> zipSearch = new ArrayList<>();
        for(long i=min;i<=max;i++){
            List<House> zipList = houseRepository.findByZipCode(i);
            zipSearch.addAll(zipList);
        }
        model.addAttribute("houseList", zipSearch);
        return "/displaytemplate";
    }
    @RequestMapping(value = "/detailed/{id}", method = RequestMethod.GET)
    public String detailedGet(@PathVariable("id") long id, Model model, User user, MessageSend messageSend){
        model.addAttribute("user", new User());
        model.addAttribute("house",new House());
        model.addAttribute("messagesender", new MessageSend());
        Sessionfullname= null;
        String aptType = null;
        Iterable<House> detailedList = houseRepository.findById(id);
        for(House itr: detailedList){
          user= userRepository.findByUsername(itr.getUsername());
            Sessionfullname = user.getFullName();
            sessionUsername = user.getUsername();
             aptType = itr.getType();
        }
        model.addAttribute("detailedList", detailedList);
        model.addAttribute("SessionName", Sessionfullname); // to display the fullname at the detailed page and used also to messagesending to save it.
        Iterable<House> suggestedAds = houseRepository.findByStateAndType(stateSession,aptType);
        model.addAttribute("suggestedList", suggestedAds);
        return "detailed";
    }
        /*The message on the detailed service*/
    @RequestMapping(value = "/messagesending", method = RequestMethod.GET)
    public String detailedPost( Model model){
        model.addAttribute("messagesender", new MessageSend());
        return "messagesending";
    }
    @RequestMapping(value = "/messagesending", method = RequestMethod.POST)
    public String message(@ModelAttribute MessageSend messageSend,  User user, Notification notification){
        messageSend.setRecieverUsername(sessionUsername);
        messageRepository.save(messageSend);
        fromSession = messageSend.getSenderEmail();
        user = userRepository.findByUsername(messageSend.getRecieverUsername()); // Find the email of the receiver
        toSession = user.getEmail();
        messageSession = messageSend.getMessage();
        try {
            sendEmailWithoutTemplating(sessionUsername, fromSession, toSession, messageSession);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
    @Autowired
    public EmailService emailService;
    public void sendEmailWithoutTemplating(String username, String sender, String receiver, String message) throws UnsupportedEncodingException {
        final DefaultEmail email = DefaultEmail.builder()
                .from(new InternetAddress("debal.felagi@gmail.com", "Debal's ADMIN"))
                .to(Lists.newArrayList(new InternetAddress(receiver, username)))
                .subject("I got someone for you")
                .body(message)
                .encoding("UTF-8").build();
        emailService.send(email);
    }

    @RequestMapping(value = "/notification", method = RequestMethod.GET)
    public String notifyGet(Model model){
        model.addAttribute("notify", new Notification());
        return "/notification";
    }
    @RequestMapping(value = "/notification", method = RequestMethod.POST)
    public String notifyPost(@ModelAttribute Notification notification){
        notificationRepository.save(notification);
        return "redirect:/";
    }

    @RequestMapping(value = "/adminPage", method = RequestMethod.GET)
    public String adminPage(){
        return "/adminpage";
    }

}