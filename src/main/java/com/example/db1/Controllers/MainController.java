package com.example.db1.Controllers;

import com.example.db1.Models.*;
import com.example.db1.Repositories.ContractRepository;
import com.example.db1.Repositories.EventRepository;
import com.example.db1.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ContractRepository contractRepository;

    @GetMapping(value={"/", "/index"})
    public String home(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("user",user);

        if(user.getRoles().toArray()[0].toString().equals("COMPANY")) {
            List<Contract> contract = contractRepository.findAll();
            List<Event> events = eventRepository.findAll();
            model.addAttribute("contract", contract);
            model.addAttribute("events", events);
            return "home_company";
        }
        else if(user.getRoles().toArray()[0].toString().equals("ADMIN")){
            List<Contract> contract = contractRepository.findAllByAuthor(user);
            boolean ok_contract=false;
            for(int i=0;i<contract.size();i++){
                if(contract.get(i).getStatus()== Status.ok){
                    ok_contract=true;
                    break;
                }
            }
            List<Event> events = eventRepository.findAllByAuthor(user);
            model.addAttribute("contract",contract);
            model.addAttribute("events",events);
            model.addAttribute("ok_contract",ok_contract);
            return "home_admin";
        }
        else{
            List<Event> events = eventRepository.findAll();
            model.addAttribute("events", events);
            return "home_participant";
        }
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String,Object> model,String role){
        User userFromDb = userRepository.findUserByEmail(user.getEmail());
        if(userFromDb!=null){
            model.put("message","User exists!");
            return "registration";
        }
        System.out.println(role);
        if(user.getEmail().equals("company")){
            user.setRoles(Collections.singleton(Role.COMPANY));
        }
        else if(role.equals("admin")){
            user.setRoles(Collections.singleton(Role.ADMIN));
        }
        else{
            user.setRoles(Collections.singleton(Role.PARTICIPANT));
        }
        user.setBalance(10000);
        user.setActive(true);
        userRepository.save(user);
        return "redirect:/login";
    }
    @PostMapping("/send")
    public String send(@AuthenticationPrincipal User user,String name,String description){
        Contract contract = new Contract(user,name,description);
        contract.setStatus(Status.wait);
        contractRepository.save(contract);
        return "redirect:/";
    }

    @PostMapping("/send_event")
    public String send_event(@AuthenticationPrincipal User user,String name,String description,Integer cost){
        Set<Long> users = new HashSet<>();
        Event event = new Event(user, name,users ,users, description);
        event.setCost(cost);
        eventRepository.save(event);
        return "redirect:/";
    }
    @PostMapping("/event_sign")
    public String event_sign(@AuthenticationPrincipal User user,Model model,String name,String age,String pcr,long event_id){
        Event event = eventRepository.findFirstById(event_id);
        event.getTempparticipants().add(user.getId());

        model.addAttribute("user",user);
        model.addAttribute("event",event);
        if(event.getCost()>user.getBalance()){
            model.addAttribute("error","Не хватает денег!");
            return "event_participant";
        }
        user.setName(name);
        user.setAge(age);
        user.setPcr(pcr);
        user.setBalance(user.getBalance()-event.getCost());
        userRepository.save(user);
        eventRepository.save(event);
        List<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);
        model.addAttribute("user", user);
        return "home_participant";
    }
    @PostMapping("/event_accept_participant")
    public String event_sign(Model model,long evid,long tpid){
        Event event = eventRepository.findFirstById(evid);
        User user = userRepository.findUserById(tpid);
        event.getTempparticipants().remove(user.getId());
        event.getParticipants().add(user.getId());
        eventRepository.save(event);

        List<User> tempParticipants = userRepository.findAllById(event.getTempparticipants());
        List<User> Participants = userRepository.findAllById(event.getParticipants());
        model.addAttribute("event",event);
        model.addAttribute("tempParticipants",tempParticipants);
        model.addAttribute("participants",Participants);
        return "event_admin";
    }
    @PostMapping(path="/contract")
    public String contract_info(@AuthenticationPrincipal User user,Model model,long test) {
        Contract contract = contractRepository.findFirstById(test);
        model.addAttribute("contract",contract);
        return "contract";
    }
    @PostMapping(path="/event")
    public String event_info(@AuthenticationPrincipal User user,Model model,long test) {
        model.addAttribute("user",user);
        Event event = eventRepository.findFirstById(test);
        model.addAttribute("event",event);
        if(user.getRoles().toArray()[0].toString().equals("COMPANY")) {
            return "event_company";
        }
        else if(user.getRoles().toArray()[0].toString().equals("ADMIN")){
            List<User> tempParticipants = userRepository.findAllById(event.getTempparticipants());
            List<User> Participants = userRepository.findAllById(event.getParticipants());
            model.addAttribute("event",event);
            model.addAttribute("tempParticipants",tempParticipants);
            model.addAttribute("participants",Participants);
            return "event_admin";
        }
        else{
            return "event_participant";
        }
    }
    @PostMapping(path="/contract_change")
    public String contract_change(@AuthenticationPrincipal User user,Model model,long test) {
        Contract contract = contractRepository.findFirstById(test);
        if(contract.getStatus()==Status.ok){
            contract.setStatus(Status.neok);
        }
        else{
            contract.setStatus(Status.ok);
        }
        contractRepository.save(contract);
        model.addAttribute("contract",contract);
        return "contract";
    }

}