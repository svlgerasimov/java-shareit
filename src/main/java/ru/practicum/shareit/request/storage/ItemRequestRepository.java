package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestor(User requestor, Sort sort);

    List<ItemRequest> findAllByRequestorIsNot(User exceptedRequestor, Pageable pageable);

    List<ItemRequest> findAllByRequestorIsNot(User exceptedRequestor, Sort sort);
}
