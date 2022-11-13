package com.university.superstore.controller;

import com.university.superstore.entity.Item;
import com.university.superstore.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    @Value("${location}")
    private String currentLocation;

    private final ItemRepository itemRepository;
    private final EntityManager entityManager;

    @GetMapping
    public String viewItems(
            @RequestParam(defaultValue = "${location}") String region,
            Model model) {

        List<Item> items;
        if (currentLocation.equals(region)) {
            items = itemRepository.findAll();
        } else if (region.equals("all")) {
            String query =
            "SELECT * FROM item\n" +
            "UNION\n" +
            "SELECT * FROM item_" + (currentLocation.equals("ge") ? "ru" : "ge");
            items = entityManager.createNativeQuery(query, Item.class).getResultList();
        } else {
            val query = "SELECT * FROM item_" + region;
            items = entityManager.createNativeQuery(query, Item.class).getResultList();
        }

        model.addAttribute("items", items);
        return "items";
    }

    @GetMapping("/item-view")
    public String itemView() {
        return "item-form";
    }

    @PostMapping("/add")
    public String addItem(
            @RequestParam("item-name") String itemName,
            @RequestParam("item-price") Integer itemPrice,
            @RequestParam("item-amount") Integer itemAmount,
            @RequestParam("item-description") String itemDescription,
            Model model
    ) {
        String query =
                "SELECT * FROM item\n" +
                        "UNION\n" +
                        "SELECT * FROM item_" + (currentLocation.equals("ge") ? "ru" : "ge");
        List<Item> items = entityManager.createNativeQuery(query, Item.class).getResultList();
        val item = items.stream()
                .sorted(Comparator.comparing(Item::getId).reversed())
                .findFirst().orElseThrow();
        val newId = String.valueOf(Integer.parseInt(item.getId()) + 1);

        val warId = itemRepository.findAll().stream()
                .findAny().orElseThrow().getWarehouseId();

        val newItem = Item.builder()
                .id(newId)
                .name(itemName)
                .price(itemPrice)
                .quantity(itemAmount)
                .warehouseId(warId)
                .description(itemDescription)
                .build();

        itemRepository.save(newItem);

        return viewItems("all", model);
    }

    @Transactional
    @PostMapping("/delete")
    public String deleteItem(@RequestParam String itemId,
                             @RequestParam(defaultValue = "${location}") String region,
                             Model model) {

        if (currentLocation.equals(region)) {
            itemRepository.deleteById(itemId);
        } else {
            val query = "DELETE FROM item_" + region + " WHERE item_id = '" + itemId + "'";
            entityManager.joinTransaction();
            entityManager.createNativeQuery(query, Item.class).executeUpdate();
        }

        return viewItems(region, model);
    }

    @GetMapping("/edit-view")
    public String editView(
        @RequestParam String itemId,
        @RequestParam(defaultValue = "${location}") String region,
        Model model
    ) {
        Item itemToEdit;

        if (currentLocation.equals(region)) {
            itemToEdit = itemRepository.findById(itemId).orElseThrow();
        } else {
            val query = "SELECT * FROM item_" + region + " WHERE item_id = '" + itemId + "'";
            itemToEdit = (Item) entityManager.createNativeQuery(query, Item.class).getResultList().get(0);
        }
        model.addAttribute("item", itemToEdit);
        model.addAttribute("region", region);
        return "item-edit";
    }

    @Transactional
    @PostMapping("/edit")
    public String editItem(
            @RequestParam("item-id") String itemId,
            @RequestParam("item-name") String itemName,
            @RequestParam("item-price") Integer itemPrice,
            @RequestParam("item-amount") Integer itemAmount,
            @RequestParam("item-description") String itemDescription,
            @RequestParam(defaultValue = "${location}") String region,
            Model model
    ) {
        String tableName = "item";

        if (!currentLocation.equals(region)) {
            tableName += "_" + region;
        }

        entityManager.joinTransaction();
        val query = entityManager.createNativeQuery(
                "UPDATE " + tableName +
                        " SET name = :name," +
                        "price = :price," +
                        "quantity = :quantity," +
                        "description = :description" +
                        " WHERE item_id = '" + itemId + "'"
                ,Item.class);
        query.setParameter("name", itemName);
        query.setParameter("price", itemPrice);
        query.setParameter("quantity", itemAmount);
        query.setParameter("description", itemDescription);
        query.executeUpdate();

        return viewItems("all", model);
    }

}
