package Controllers;
import Model.InHouse;
import Model.Inventory;
import Model.OutSourced;
import Model.Part;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ModifyPartController implements Initializable {
    Inventory inv;
    Part part;

    @FXML private RadioButton inHouseRadio;
    @FXML private RadioButton outSourcedRadio;
    @FXML private Label companyLabel;
    @FXML private TextField id;
    @FXML private TextField name;
    @FXML private TextField price;
    @FXML private TextField count;
    @FXML private TextField min;
    @FXML private TextField max;
    @FXML private TextField company;
    @FXML private Button modifyPartSaveButton;

    public ModifyPartController(Inventory inv, Part part) {
        this.inv = inv;
        this.part = part;
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setData();
    }
    private void setData() {

        if (part instanceof InHouse) {

            InHouse part1 = (InHouse) part;
            inHouseRadio.setSelected(true);
            companyLabel.setText("Machine ID");
            this.name.setText(part1.getName());
            this.id.setText((Integer.toString(part1.getPartID())));
            this.count.setText((Integer.toString(part1.getInStock())));
            this.price.setText((Double.toString(part1.getPrice())));
            this.min.setText((Integer.toString(part1.getMin())));
            this.max.setText((Integer.toString(part1.getMax())));
            this.company.setText((Integer.toString(part1.getMachineID())));

        }

        if (part instanceof OutSourced) {

            OutSourced part2 = (OutSourced) part;
            outSourcedRadio.setSelected(true);
            companyLabel.setText("Company Name");
            this.name.setText(part2.getName());
            this.id.setText((Integer.toString(part2.getPartID())));
            this.count.setText((Integer.toString(part2.getInStock())));
            this.price.setText((Double.toString(part2.getPrice())));
            this.min.setText((Integer.toString(part2.getMin())));
            this.max.setText((Integer.toString(part2.getMax())));
            this.company.setText(part2.getCompanyName());
        }
    }
    private void inHouseItemUpdate() {
        inv.updatePart(new InHouse(Integer.parseInt(id.getText().trim()),
                name.getText().trim(),
                Double.parseDouble(price.getText().trim()),
                Integer.parseInt(count.getText().trim()),
                Integer.parseInt(min.getText().trim()),
                Integer.parseInt(max.getText().trim()),
                Integer.parseInt(company.getText().trim())));
    }
    private void outSourcedItemUpdate() {
        inv.updatePart(new OutSourced(Integer.parseInt(id.getText().trim()),
                name.getText().trim(),
                Double.parseDouble(price.getText().trim()),
                Integer.parseInt(count.getText().trim()),
                Integer.parseInt(min.getText().trim()),
                Integer.parseInt(max.getText().trim()),
                company.getText().trim()));
    }
    private void mainScreen(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MainScreen.fxml"));
            MainScreenController controller = new MainScreenController(inv);

            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {

        }
    }
    private boolean checkValue(TextField field) {
        boolean error = false;
        try {
            if (field.getText().trim().isEmpty() || field.getText().trim() == null) {
                ErrorMessages.errorPart(1, field);
                return true;
            }
            if (field == price && Double.parseDouble(field.getText().trim()) <= 0.0) {
                ErrorMessages.errorPart(5, field);
                error = true;
            }
        } catch (Exception e) {
            error = true;
            ErrorMessages.errorPart(3, field);
            System.out.println(e);

        }
        return error;
    }
    private boolean checkType(TextField field) {

        if (field == price & !field.getText().trim().matches("\\d+(\\.\\d+)?")) {
            ErrorMessages.errorPart(3, field);
            return true;
        }
        if (field != price & !field.getText().trim().matches("[0-9]*")) {
            ErrorMessages.errorPart(3, field);
            return true;
        }
        return false;

    }
    private boolean cancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("Click ok to confirm");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }
    @FXML
    private void clearTextField(MouseEvent event) {
        Object source = event.getSource();
        TextField field = (TextField) source;
        field.setText("");
    }
    @FXML
    private void selectInHouse(MouseEvent event) {
        companyLabel.setText("Machine ID");

    }
    @FXML
    private void selectOutSourced(MouseEvent event) {
        companyLabel.setText("Company Name");

    }
    @FXML
    private void idDisabled(MouseEvent event) {
    }
    @FXML
    private void cancelModifyPart(MouseEvent event) {
        boolean cancel = cancel();
        if (cancel) {
            mainScreen(event);
        } else {
            return;
        }
    }
    @FXML
    private void saveModifyPart(MouseEvent event) {
        boolean end = false;
        TextField[] fieldCount = {count, price, min, max};
        if (inHouseRadio.isSelected() || outSourcedRadio.isSelected()) {
            for (int i = 0; i < fieldCount.length; i++) {
                boolean valueError = checkValue(fieldCount[i]);
                if (valueError) {
                    end = true;
                    break;
                }
                boolean typeError = checkType(fieldCount[i]);
                if (typeError) {
                    end = true;
                    break;
                }
            }
            if (name.getText().trim().isEmpty() || name.getText().trim().toLowerCase().equals("part name")) {
                ErrorMessages.errorPart(4, name);
                return;
            }
            if (Integer.parseInt(min.getText().trim()) > Integer.parseInt(max.getText().trim())) {
                ErrorMessages.errorPart(8, min);
                return;
            }
            if (Integer.parseInt(count.getText().trim()) < Integer.parseInt(min.getText().trim())) {
                ErrorMessages.errorPart(6, count);
                return;
            }
            if (Integer.parseInt(count.getText().trim()) > Integer.parseInt(max.getText().trim())) {
                ErrorMessages.errorPart(7, count);
                return;
            }

            if (end) {
                return;
            } else if (outSourcedRadio.isSelected() && company.getText().trim().isEmpty()) {
                ErrorMessages.errorPart(1, company);
                return;
            } else if (inHouseRadio.isSelected() && !company.getText().matches("[0-9]*")) {
                ErrorMessages.errorPart(9, company);
                return;

            } else if (inHouseRadio.isSelected() & part instanceof InHouse) {
                inHouseItemUpdate();
            } else if (inHouseRadio.isSelected() & part instanceof OutSourced) {
                inHouseItemUpdate();
            } else if (outSourcedRadio.isSelected() & part instanceof OutSourced) {
                outSourcedItemUpdate();
            } else if (outSourcedRadio.isSelected() & part instanceof InHouse) {
                outSourcedItemUpdate();
            }

        } else {
            ErrorMessages.errorPart(2, null);
            return;

        }
        mainScreen(event);
    }
}