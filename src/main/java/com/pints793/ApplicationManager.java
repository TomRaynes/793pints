package com.pints793;

import com.pints793.cellar.Cellar;
import org.springframework.stereotype.Component;

@Component
public class ApplicationManager extends ApplicationSupport {

    public void run() {
        while (Thread.currentThread().isAlive()) {
            Cellar cellar = cellarCollection.findById("CELLAR-0000019c-53fb-a958-9445-53da2d67bccc").orElse(null);
            System.out.println(cellar.getName());





            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
