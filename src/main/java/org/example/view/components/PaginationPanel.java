package org.example.view.components;

import javax.swing.*;
import java.awt.*;

public class PaginationPanel extends JPanel {
    private final JButton prevButton;
    private final JButton nextButton;
    private final JLabel pageLabel;
    private int currentPage = 1;
    private int totalPages = 1;
    private final Runnable onPageChange;

    public PaginationPanel(Runnable onPageChange) {
        this.onPageChange = onPageChange;
        setLayout(new FlowLayout(FlowLayout.CENTER));

        prevButton = new JButton("← Previous");
        nextButton = new JButton("Next →");
        pageLabel = new JLabel("Page 1 of 1");

        prevButton.addActionListener(e -> previousPage());
        nextButton.addActionListener(e -> nextPage());

        add(prevButton);
        add(pageLabel);
        add(nextButton);

        updateButtons();
    }

    public void setTotalItems(int totalItems, int pageSize) {
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;
        updatePageLabel();
        updateButtons();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePageLabel();
            updateButtons();
            onPageChange.run();
        }
    }

    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePageLabel();
            updateButtons();
            onPageChange.run();
        }
    }

    private void updatePageLabel() {
        pageLabel.setText(String.format("Page %d of %d", currentPage, totalPages));
    }

    private void updateButtons() {
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }
}