package org.telegram.forcesub.service;


import org.springframework.stereotype.Service;
import org.telegram.forcesub.entity.Subcription;
import org.telegram.forcesub.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetChannelLinkService {

    private final SubscriptionRepository subscriptionRepository;

    public GetChannelLinkService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<String> getChannelLinks(List<String> channelIds) {

        List<String> channelLinks = new ArrayList<>();
        for (String channelId : channelIds) {
            Subcription byChannelId = subscriptionRepository.findByChannelId(channelId).getFirst();
            channelLinks.add(byChannelId.getChannelLink());
        }
        return channelLinks;
    }
}
