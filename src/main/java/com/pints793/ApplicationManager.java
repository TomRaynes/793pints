package com.pints793;

import com.pints793.cask.Cask;
import com.pints793.cellar.Cellar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class ApplicationManager extends ApplicationSupport {

    public void run() {
        while (Thread.currentThread().isAlive()) {
            updateCaskStates();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateCaskStates() {
        int page = 0;
        Page<Cask> result;
        OffsetDateTime currentTime = OffsetDateTime.now();

        do {
            result = caskCollection.findAll(PageRequest.of(page++, 500));

            for (Cask cask : result.getContent()) {
                Long cooldown = cask.getActiveCooldown();

                if (!cask.getState().hasCooldown() || cooldown == null) {
                    continue;
                }
                if (currentTime.isAfter(cask.getStateChangeTime().plusHours(cooldown))) {
                    cask.progressState();
                    caskCollection.save(cask);
                }
            }
        } while (result.hasNext());

        System.out.println(currentTime);
    }
}
