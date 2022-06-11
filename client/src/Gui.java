import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.ArrayList;
import java.util.HashMap;

import static java.awt.Component.CENTER_ALIGNMENT;

class Response {
    private String name;
    private String address;
    private String phoneNumber;
    private String dateOfBirth;
    private String backwardsName;
    private String phoneNumberSum;
    private String dateOfBirthLeapYearDescription;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getBackwardsName() {
        return backwardsName;
    }

    public String getPhoneNumberSum() {
        return phoneNumberSum;
    }

    public String getDateOfBirthLeapYearDescription() {
        return dateOfBirthLeapYearDescription;
    }

    public Response(String name, String address, String phoneNumber, String dateOfBirth) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.backwardsName = (new StringBuilder(name)).reverse().toString();
        this.phoneNumberSum = Integer.toString(phoneNumber.chars().map(Character::getNumericValue).sum());

        int YearOfBirth = Integer.parseInt(dateOfBirth.substring(0, 4));
        String textIfLeapYear = "You were born in a leap year";
        String textIfNotLeapYear = "You were not born in a leap year";

        if (YearOfBirth % 400 == 0) {
            this.dateOfBirthLeapYearDescription = textIfLeapYear;
        } else if (YearOfBirth % 100 == 0) {
            this.dateOfBirthLeapYearDescription = textIfNotLeapYear;
        } else if (YearOfBirth % 4 == 0) {
            this.dateOfBirthLeapYearDescription = textIfLeapYear;
        } else {
            this.dateOfBirthLeapYearDescription = textIfNotLeapYear;
        }
    }
}

class Gui {

    public static void main(String args[]){
        JFrame frame = new JFrame("Triona");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,600);

        JPanel panel = new JPanel();
        JLabel responseLabel = new JLabel();

        JLabel NameResultLabel = new JLabel();
        JLabel AddressResultLabel = new JLabel();
        JLabel PhoneNumberResultLabel = new JLabel();
        JLabel DateOfBirthResultLabel = new JLabel();
        JLabel BackwardsNameLabel = new JLabel();
        JLabel PhoneNumberSumLabel = new JLabel();
        JLabel LeapYearLabel = new JLabel();


        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

        panel.setLayout(boxLayout);

        JLabel nameLabel = new JLabel();
        nameLabel.setText("Name");
        panel.add(nameLabel);

        JTextField nameField = new JTextField();
        panel.add(nameField);

        JLabel AddressLabel = new JLabel();
        AddressLabel.setText("Address");
        panel.add(AddressLabel);

        JTextField addressField = new JTextField();
        panel.add(addressField);

        JLabel phoneNumberLabel = new JLabel();
        phoneNumberLabel.setText("Phone Number");
        panel.add(phoneNumberLabel);

        JTextField phoneNumberField = new JTextField();
        panel.add(phoneNumberField);

        JLabel dateOfBirthLabel = new JLabel();
        dateOfBirthLabel.setText("Date of Birth");
        panel.add(dateOfBirthLabel);

        JTextField dateOfBirthField = new JTextField();
        panel.add(dateOfBirthField);

        JButton submitButton = new JButton("Submit");
        ButtonModel submitButtonModel = submitButton.getModel();
        submitButtonModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (submitButtonModel.isPressed()) {
                    responseLabel.setText("Data submitted");
                    NameResultLabel.setText("");
                    AddressResultLabel.setText("");
                    PhoneNumberResultLabel.setText("");
                    DateOfBirthResultLabel.setText("");
                    BackwardsNameLabel.setText("");
                    PhoneNumberResultLabel.setText("");
                    LeapYearLabel.setText("");
                }
            }
        });
        panel.add(submitButton);
        JButton searchButton = new JButton("Search");
        ButtonModel searchButtonModel = searchButton.getModel();
        searchButtonModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (searchButtonModel.isPressed()) {
                    Response response = new Response(nameField.getText(), addressField.getText(), phoneNumberField.getText(), dateOfBirthField.getText());
                    responseLabel.setText("Results:");
                    NameResultLabel.setText("Name: "+response.getName());
                    AddressResultLabel.setText("Address: "+response.getAddress());
                    PhoneNumberResultLabel.setText("Phone Number: "+response.getPhoneNumber());
                    DateOfBirthResultLabel.setText("Date of Birth: "+response.getDateOfBirth());
                    BackwardsNameLabel.setText("Backwards Name: "+response.getBackwardsName());
                    PhoneNumberSumLabel.setText("Phone Number Sum: "+response.getPhoneNumberSum());
                    LeapYearLabel.setText(response.getDateOfBirthLeapYearDescription());
                }
            }
        });
        panel.add(searchButton);

        panel.add(responseLabel);

        panel.add(NameResultLabel);
        panel.add(AddressResultLabel);
        panel.add(PhoneNumberResultLabel);
        panel.add(DateOfBirthResultLabel);
        panel.add(BackwardsNameLabel);
        panel.add(PhoneNumberSumLabel);
        panel.add(LeapYearLabel);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}