package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(Long requestorId);

    @Query("SELECT COUNT(r) FROM ItemRequest r WHERE r.requestor.id = ?1")
    int findAmountOfRequests(Long userId);

    @Query("SELECT r FROM ItemRequest r WHERE r.requestor.id <> ?1")
    List<ItemRequest> findAllInPage(Long userId, Pageable pageable);
}
