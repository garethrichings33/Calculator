package main.java;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator implements ActionListener {
    public static void main(String[] args){
        new Calculator();
    }

    private JFrame frame;
    private JTextField textField;

    private int maxChar = 50;
    private char[] input = new char[maxChar];
    private int numberOfInputChars = 0;
    private double[] numbers = new double[maxChar];
    private char[] operators = new char[maxChar];
    private int numberOfOperators;
    private int numberOfOperands;

    public Calculator() {
        frame = new JFrame("Calculator");

        textField = new JTextField();
        textField.setEditable(false);
        textField.setBounds(10, 20, 190, 40);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        frame.add(textField);

        addButton("AC",10,70,40,40);
        addButton("DEL",60,70,40,40);
        addButton("%",110,70,40,40);
        addButton("/",160,70,40,40);
        addButton("7",10,120,40,40);
        addButton("8",60,120,40,40);
        addButton("9",110,120,40,40);
        addButton("x",160,120,40,40);
        addButton("4",10,170,40,40);
        addButton("5",60,170,40,40);
        addButton("6",110,170,40,40);
        addButton("-",160,170,40,40);
        addButton("1",10,220,40,40);
        addButton("2",60,220,40,40);
        addButton("3",110,220,40,40);
        addButton("+",160,220,40,40);
        addButton("±",10, 270, 40, 40);
        addButton("0",60,270,40,40);
        addButton(".",110,270,40,40);
        addButton("=",160,270,40,40);

        frame.setLayout(null);
        frame.setSize(210,360);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void addButton(String text, int x, int y, int width, int height){
        JButton button = new JButton(text);
        button.setBounds(x,y,width,height);
        button.addActionListener(this);
        frame.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String buttonPressed = event.getActionCommand().toString();
        if(buttonPressed == "AC"){
            emptyInput();
        }
        else if (buttonPressed == "DEL") {
            deleteInputChar();
        }
        else if (buttonPressed == "±") {
            changeSign();
        }
        else if (buttonPressed == "%") {
            convertToPercentage();
        }
        else if (buttonPressed == "="){
            calculate();
            return;
        }
        else {
            addInputChar(buttonPressed);
        }

        textField.setText(new String(input, 0, numberOfInputChars));
    }

    private void emptyInput(){
        for(int i = 0; i < maxChar; i++)
            input[i] = ' ';
        numberOfInputChars = 0;
    }

    private void deleteInputChar(){
        if(numberOfInputChars > 0) {
            numberOfInputChars--;
            input[numberOfInputChars] = ' ';
        }
    }

    private void addInputChar(String buttonPressed){
        input[numberOfInputChars++] = buttonPressed.charAt(0);
    }

    private void changeSign(){
        int arrayIndex = numberOfInputChars-1;
        if(isOperator(input[arrayIndex])) return;

        while(!isOperator(input[arrayIndex]) && arrayIndex > 0)
            arrayIndex--;

        if(arrayIndex == 0) {
            if (input[arrayIndex] == '-')
                removeMinus(arrayIndex);
            else if (isOperator(input[arrayIndex])) {
                textField.setText("Error");
            }
            else
                insertMinus(arrayIndex);
        }
        else if (input[arrayIndex] == '+')
            input[arrayIndex] = '-';

        else if(input[arrayIndex] == '-'){
            if(!isOperator(input[arrayIndex-1]))
                input[arrayIndex] = '+';
            else
                removeMinus(arrayIndex);
        }
        else
            insertMinus(arrayIndex+1);
    }

    private void convertToPercentage(){
        int arrayIndex = numberOfInputChars-1;
        if(isOperator(input[arrayIndex])) return;

        while(!isOperator(input[arrayIndex]) && arrayIndex > 0)
            arrayIndex--;
        if(isOperator(input[arrayIndex])) arrayIndex++;

        int numberOfDecimals = 0;
        StringBuilder temp = new StringBuilder();
        for(int i = arrayIndex; i<numberOfInputChars; i++) {
            if(input[i] == '.') numberOfDecimals++;
            if(numberOfDecimals>1) return;

            temp.append(input[i]);
        }
        double number = Double.parseDouble(temp.toString());
        number /= 100;
        temp.setLength(0);
        temp.append(number);
        for(int i = arrayIndex; i < arrayIndex + temp.length(); i++)
            input[i] = temp.charAt(i-arrayIndex);
        numberOfInputChars = arrayIndex + temp.length();
    }

    private void insertMinus(int index){
        for(int i = numberOfInputChars-1; i >= index; i--)
            input[i+1] = input[i];
        input[index] = '-';
        numberOfInputChars++;
    }

    private void removeMinus(int index){
        for(int i = index; i < numberOfInputChars-1; i++)
            input[i] = input[i+1];
        input[numberOfInputChars-1] = ' ';
        numberOfInputChars--;
    }
    private void calculate(){
        if(!inputIsValid()) {
            textField.setText("Error");
            return;
        }
        parseInput();
        division();
        multiplication();
        subtraction();
        addition();

        setResult();
    }

    private void setResult(){
        String result = Double.toString(numbers[0]);
        textField.setText(result);
        for(int i = 0; i < result.length(); i++)
            input[i] = result.charAt(i);
        numberOfInputChars = result.length();
    }

    private void division(){
        for(int i = 0; i < numberOfOperators; i++){
            if(operators[i] == '/'){
                numbers[i+1] = numbers[i] / numbers[i+1];
                numbers[i] = Double.MAX_VALUE;
            }
        }
        arraysShift('/');
    }

    private void multiplication(){
        for(int i = 0; i < numberOfOperators; i++){
            if(operators[i] == 'x'){
                numbers[i+1] = numbers[i] * numbers[i+1];
                numbers[i] = Double.MAX_VALUE;
            }
        }
        arraysShift('x');
    }

    private void subtraction(){
        for(int i = 0; i < numberOfOperators; i++){
            if(operators[i] == '-'){
                numbers[i+1] = numbers[i] - numbers[i+1];
                numbers[i] = Double.MAX_VALUE;
            }
        }
        arraysShift('-');
    }

    private void addition(){
        for(int i = 0; i < numberOfOperators; i++){
            if(operators[i] == '+'){
                numbers[i+1] = numbers[i] + numbers[i+1];
                numbers[i] = Double.MAX_VALUE;
            }
        }
        arraysShift('+');
    }

    private void arraysShift(char operator){
        char[] tempOperators = new char[numberOfOperators];
        double[] tempNumbers = new double[numberOfOperands];

        int tempNumberOfOperators = 0;

        for(int i = 0; i < numberOfOperators; i++)
            if(operators[i] != operator)
                tempOperators[tempNumberOfOperators++] = operators[i];

        numberOfOperators = tempNumberOfOperators;
        for(int i = 0; i < numberOfOperators; i++)
            operators[i] = tempOperators[i];

        int tempNumberOfOperands = 0;
        for(int i = 0; i < numberOfOperands; i++)
            if (numbers[i] != Double.MAX_VALUE)
                tempNumbers[tempNumberOfOperands++] = numbers[i];

        numberOfOperands = tempNumberOfOperands;
        for(int i = 0; i<numberOfOperands; i++)
            numbers[i] = tempNumbers[i];
    }

    private void parseInput(){
        StringBuilder temp = new StringBuilder();
        int lastOperatorIndex = -1;
        int nextOperatorIndex = -1;
        numberOfOperators = 0;
        numberOfOperands = 0;
        boolean getNumber = false;

        int i = 0;
        while(i < numberOfInputChars){
            if(input[i] == '-' && (i == 0 || isOperator(input[i-1]))){
                i++;
                continue;
            }
            else if(isOperator(input[i])) {
                operators[numberOfOperators++] = input[i];
                lastOperatorIndex = nextOperatorIndex;
                nextOperatorIndex = i;
                getNumber = true;
            }
            else if (i == numberOfInputChars-1) {
                lastOperatorIndex = nextOperatorIndex;
                nextOperatorIndex = numberOfInputChars;
                getNumber = true;
            }

            if(getNumber) {
                temp.setLength(0);
                for (int j = lastOperatorIndex + 1; j < nextOperatorIndex; j++)
                    temp.append(input[j]);
                numbers[numberOfOperands++] = Double.parseDouble(temp.toString());
                getNumber = false;
            }
            i++;
        }
    }

    private boolean inputIsValid(){
        int finalCharacterIndex = numberOfInputChars - 1;

        if(isOperatorNotMinus(input[0])
        || isOperator(input[finalCharacterIndex]))
            return false;

        int numberOfDecimals = 0;
        for(int i = 0; i <= finalCharacterIndex; i++){
            if(isOperator(input[i])){
                if(isOperatorNotMinus(input[i+1]))
                    return false;
                numberOfDecimals = 0;
            }

            if(isDecimalPoint(input[i]))
                numberOfDecimals++;

            if(numberOfDecimals > 1)
                return false;
        }
        return true;
    }

    private boolean isOperator(char character){
        return character == '+'
                || character == '-'
                || character == 'x'
                || character == '/';
    }

    private boolean isOperatorNotMinus(char character){
        return character == '+'
                || character == 'x'
                || character == '/';
    }


    private boolean isDecimalPoint(char character) {
        return character == '.';
    }
}
