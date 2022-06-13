import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.net.http.HttpClient;
import java.util.concurrent.Flow;

import static java.time.temporal.ChronoUnit.SECONDS;

class Person {
    private String name;
    private String address;
    private String phoneNumber;
    private String dateOfBirth;

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
        return (new StringBuilder(name)).reverse().toString();
    }

    public String getPhoneNumberSum() {
        return Integer.toString( phoneNumber.chars().map(Character::getNumericValue).sum() );
    }

    public String getDateOfBirthLeapYearDescription() {
        if (dateOfBirth.length() < 4) {
            return "";
        }
        int YearOfBirth = Integer.parseInt(dateOfBirth.substring(0, 4));
        String textIfLeapYear = "Person was born in a leap year";
        String textIfNotLeapYear = "Person was not born in a leap year";

        if (YearOfBirth % 400 == 0) {
            return textIfLeapYear;
        } else if (YearOfBirth % 100 == 0) {
            return textIfNotLeapYear;
        } else if (YearOfBirth % 4 == 0) {
            return textIfLeapYear;
        } else {
            return textIfNotLeapYear;
        }
    }

    public String toJSON() {
        return "{\"name\":\""+name+"\",\"address\":\""+address+"\",\"phoneNumber\":\""+
                phoneNumber+"\",\"dateOfBirth\":\""+dateOfBirth+"\"}";
    }

    public void submit() {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8000/person"))
                    .timeout(Duration.of(10, SECONDS))
                    .POST(HttpRequest.BodyPublishers.ofString(toJSON()))
                    .header("Content-Type", "application/json").build();

            HttpResponse<String> httpResponse = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    public Person search() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8000/person?name="+name.replace(' ','_')))
                .timeout(Duration.of(10, SECONDS)).GET().build();
        HttpResponse<String> httpResponse = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
        return new Person(httpResponse.body());
    }

    public Person(String name, String address, String phoneNumber, String dateOfBirth) {
        this.name = (name != null ? name : "");
        this.address = (address != null ? address : "");
        this.phoneNumber = (phoneNumber != null ? phoneNumber : "");
        this.dateOfBirth = (dateOfBirth != null ? dateOfBirth : "");
    }
    public Person(String json) {
        // Remove curly braces

        json = json.substring(1, json.length()-1);

        if (json.length() == 0) {
            throw new IllegalArgumentException();
        }

        String[] keysAndValues = json.split(",");

        for (String keyAndValue: keysAndValues) {
            String[] param = keyAndValue.split(":");
            switch (param[0].substring(1, param[0].length()-1)) {
                case "name" -> this.name = param[1].substring(1, param[1].length()-1);
                case "address" -> this.address = param[1].substring(1, param[1].length()-1);
                case "phoneNumber" -> this.phoneNumber = param[1].substring(1, param[1].length()-1);
                case "dateOfBirth" -> this.dateOfBirth = param[1].substring(1, param[1].length()-1);
            }
        }
    }

    public String toString() {
        return "Name: %s, Address: %s, Phone Number: %s, Date of Birth: %s".formatted(name, address, phoneNumber, dateOfBirth);
    }
}

class Gui {

    JFrame frame = new JFrame("Triona");
    JPanel panel = new JPanel();
    JLabel responseLabel = new JLabel();

    JLabel NameResultLabel = new JLabel();
    JLabel AddressResultLabel = new JLabel();
    JLabel PhoneNumberResultLabel = new JLabel();
    JLabel DateOfBirthResultLabel = new JLabel();
    JLabel BackwardsNameLabel = new JLabel();
    JLabel PhoneNumberSumLabel = new JLabel();
    JLabel LeapYearLabel = new JLabel();
    JLabel nameLabel = new JLabel();
    JTextField nameField = new JTextField();
    JLabel AddressLabel = new JLabel();
    JTextField addressField = new JTextField();
    JTextField phoneNumberField = new JTextField();
    JLabel dateOfBirthLabel = new JLabel();
    JTextField dateOfBirthField = new JTextField();
    JButton submitButton = new JButton("Submit");
    BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);


    public Gui() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,600);



        panel.setLayout(boxLayout);


        nameLabel.setText("Name");
        panel.add(nameLabel);


        panel.add(nameField);


        AddressLabel.setText("Address");
        panel.add(AddressLabel);


        panel.add(addressField);

        JLabel phoneNumberLabel = new JLabel();
        phoneNumberLabel.setText("Phone Number");
        panel.add(phoneNumberLabel);


        panel.add(phoneNumberField);


        dateOfBirthLabel.setText("Date of Birth");
        panel.add(dateOfBirthLabel);


        panel.add(dateOfBirthField);


        ButtonModel submitButtonModel = submitButton.getModel();
        submitButtonModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (submitButtonModel.isPressed()) {
                    getInput().submit();
                    clearOutput();
                    responseLabel.setText("Data submitted");
                }
            }
        });
        panel.add(submitButton);
        JButton searchButton = new JButton("Search (Name Only)");
        ButtonModel searchButtonModel = searchButton.getModel();
        searchButtonModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (searchButtonModel.isPressed()) {
                    try {
                        Person response = getInput().search();
                        responseLabel.setText("Results:");
                        setOutput(response);
                    } catch (Exception exception) {
                        System.out.println(exception);
                    }



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

    public Person getInput() {
        return new Person(nameField.getText(), addressField.getText(), phoneNumberField.getText(), dateOfBirthField.getText());
    }

    public void setOutput(Person person) {
        NameResultLabel.setText("Name: "+person.getName());
        AddressResultLabel.setText("Address: "+person.getAddress());
        PhoneNumberResultLabel.setText("Phone Number: "+person.getPhoneNumber());
        DateOfBirthResultLabel.setText("Date of Birth: "+person.getDateOfBirth());
        BackwardsNameLabel.setText("Backwards Name: "+person.getBackwardsName());
        PhoneNumberSumLabel.setText("Phone Number Sum: "+person.getPhoneNumberSum());
        LeapYearLabel.setText(person.getDateOfBirthLeapYearDescription());
    }

    public void clearOutput() {
        responseLabel.setText("");
        NameResultLabel.setText("");
        AddressResultLabel.setText("");
        PhoneNumberResultLabel.setText("");
        DateOfBirthResultLabel.setText("");
        BackwardsNameLabel.setText("");
        PhoneNumberSumLabel.setText("");
        LeapYearLabel.setText("");
    }

    public static void main(String args[]){
        Gui gui = new Gui();

    }
}