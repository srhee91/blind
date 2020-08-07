package org.kiworkshop.blind.notification.api;

import java.net.URI;

import org.kiworkshop.blind.notification.service.WatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/watch")
@RestController
public class WatchController {
    private final WatchService watchService;

    @PostMapping
    public ResponseEntity<Long> startWatch(@RequestParam Long postId, @RequestParam Long userId) {
        Long watchId = watchService.watch(postId, userId);
        return ResponseEntity
            .created(URI.create("/api/watch/" + watchId))
            .body(watchId);
    }

    @GetMapping
    public ResponseEntity<Long> fetchWatchId(@RequestParam Long postId, @RequestParam Long userId) {
        Long watchId = watchService.getWatchId(postId, userId);
        return ResponseEntity.ok(watchId);
    }

    @DeleteMapping("/{watchId}")
    public ResponseEntity<Void> stopWatch(@PathVariable("watchId") Long watchId, @RequestParam Long userId) {
        watchService.cancelWatch(watchId, userId);
        return ResponseEntity.noContent().build();
    }
}
