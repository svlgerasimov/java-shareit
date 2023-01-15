package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner(User owner, Pageable pageable);

    @Query(value = "select it " +
            "from Item as it " +
            "where (lower(it.name) like %:text% or lower(it.description) like %:text%) and it.available=true")
    List<Item> search(@Param("text") String textInLowerCase, Pageable pageable);

    Optional<Item> findByIdAndOwnerIdNot(Long id, Long ownerId);

    List<Item> findAllByRequest(ItemRequest request);

    List<Item> findAllByRequestIn(List<ItemRequest> requests);
}
