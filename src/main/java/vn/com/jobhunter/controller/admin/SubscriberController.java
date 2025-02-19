package vn.com.jobhunter.controller.admin;

import jakarta.validation.Valid;
import vn.com.jobhunter.domain.Subscriber;
import vn.com.jobhunter.service.SubscriberService;
import vn.com.jobhunter.util.SecurityUtil;
import vn.com.jobhunter.util.annotation.ApiMessage;
import vn.com.jobhunter.util.error.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a subscriber")
    public ResponseEntity<Subscriber> createNewSubscriber(@Valid @RequestBody Subscriber sub)
            throws IdInvalidException {
        boolean isExist = this.subscriberService.isExistsByEmail(sub.getEmail());
        if (isExist == true) {
            throw new IdInvalidException("Email " + sub.getEmail() + " đã tồn tại");
        }

        Subscriber newSub = this.subscriberService.handleCreateSubscriber(sub);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSub);
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subsRequest) throws IdInvalidException {
        Subscriber subsDB = this.subscriberService.findSubscriberById(subsRequest.getId());
        if (subsDB == null) {
            throw new IdInvalidException("Id " + subsRequest.getId() + " không tồn tại");
        }

        Subscriber currentSubscriber = this.subscriberService.handleUpdateSubscriber(subsDB, subsRequest);
        return ResponseEntity.ok().body(currentSubscriber);
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }

}
