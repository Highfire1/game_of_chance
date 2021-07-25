package sample;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    public Random random_generator = new Random();

    public Button roll_dice_button;
    public Label roll_dice_label;
    public Label money_label;
    public Label money_label_prefix;
    public Label story_label;
    public ImageView image_id;

    int stage = 0;
    Integer money = 1500;
    boolean gameover = false;

    // these probably belong in a class or method but too late now
    public Image image_intro;
    public Image image_stage1;
    public Image image_stage2;
    public Image image_victory;
    public Image image_defeat;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        image_intro = get_image( "picture1.png");
        image_stage1 = get_image("picture2.png");
        image_stage2 = get_image("picture3.png");
        image_victory = get_image("victory.jpg");
        image_defeat = get_image("loss.jpg");

        story_packer("You arrive in Vegas with $1500 to your name.");
        roll_dice_button.setText("Enter Vegas.");
        image_id.setImage(image_intro);
    }

    // helper function to get Images from the asset folder
    public Image get_image(String img_name) {
        return new Image(getClass().getResourceAsStream("/assets/" + img_name));
    }

    // below, but default to half a second
    public void story_packer_wrappper(String new_entry) {
        story_packer_wrapper(new_entry, 300);
    }

    // a wrapper for story_packer with a timer
    public void story_packer_wrapper(String new_entry, int delay) {
        // has two modes, one of which should really be in its own method but too late now

        if (gameover) {
            new Timeline(new KeyFrame(
                    Duration.millis(delay),
                    ae -> story_packer(new_entry)))
                    .play();
        } else {
            story_packer(new_entry);
            roll_dice_button.setDisable(true);

            new Timeline(new KeyFrame(
                    Duration.millis(delay),
                    ae -> roll_dice_button.setDisable(gameover)))
                    .play();
        }

    }


    // Requires a String
    // Modifies story_label
    // To be exact, adds strings to the label on the left in a way so they won't get cut off by ellipses
    // should probably have been a textfield but too late now
    public void story_packer(String new_entry) {
        String label = "";
        FontLoader fontLoader =  Toolkit.getToolkit().getFontLoader();
        String cur_line = "";

        // iterate through every word, adding a newline if story_label_width is exceeded
        // yes the logic is cursed but what's important is that it works and the window can't be resized anyways
        for(String word : new_entry.split(" ")) {

            double story_label_width = story_label.prefWidth(-1)/2 + 20; // dark magic
            float cur_line_width = fontLoader.computeStringWidth(cur_line + word, story_label.getFont());

            if (cur_line_width > story_label_width) {
                label += cur_line + "\n";
                cur_line = "";
            }
            cur_line += word + " ";
        }
        label += cur_line;
        String new_label = label + "\n\n" + story_label.getText();
        story_label.setText(new_label);
    }

    // here is the entirety of the game logic, oop is overrated anyways (jk)
    // The "level" of the game is stored as an int in stage
    public void roll_dice_method(ActionEvent actionEvent) {
        switch(stage) {
            case 0:
                story_packer("You are surrounded by an array of slot machines.");
                roll_dice_button.setText("Gamble $10");
                roll_dice_label.setText("No backing out now.");
                image_id.setImage(image_stage1);
                stage++;
                break;
            case 1:
                int winnings;

                // mhm completely realistic odds lie here
                // 1/10 odds to win 1000$
                // 3/10 odds to win 100$
                // increase bound to increase playtime lol
                switch(random_generator.nextInt(10)) {
                    case 1:
                        story_packer_wrappper("You win the jackpot of 1000$!!!");
                        winnings = 990;
                        break;
                    case 2:
                    case 3:
                    case 4:
                        story_packer_wrappper("You win $100!");
                        winnings = 90;
                        break;
                    default:
                        story_packer_wrappper("You win nothing...");
                        winnings = -10;
                }
                add_money(winnings);

                if (money > 5000) {
                    story_packer("For just one day, your luck seems to be endless. You decide to raise the stakes...");
                    image_id.setImage(image_stage2);
                    roll_dice_button.setText("Place a thousand on black.");
                    roll_dice_label.setText("You have nothing to lose.");
                    stage++;
                }
                break;
            case 2:
                Integer randint = random_generator.nextInt(37);

                // real roulette table logic!!!
                if (randint == 0) {
                    story_packer_wrappper("0: The house wins.");
                    winnings = -1000;
                } else if (randint % 2 == 0) {
                    story_packer_wrappper(randint.toString() + ": You lose.");
                    winnings = -1000;
                } else {
                    story_packer_wrappper(randint.toString() + ": You win!");
                    winnings = 1000;
                }
                add_money(winnings);

                if (money > 10000) {
                    gameover = true;
                    image_id.setImage(image_victory);
                    story_packer("You've made it... but at what cost...");
                    story_packer_wrapper("THANK YOU FOR PLAYING!", 1000);
                    story_packer_wrapper("WRITTEN BY ANDERSON!", 2500);
                    story_packer_wrapper("ILLUSTRATED BY ANDERSON!", 4000);
                    story_packer_wrapper("STOCK PHOTOS FROM UNSPLASH.COM!", 5500);
                    story_packer_wrapper("CODED BY ANDERSON!", 7000);
                    story_packer_wrapper("SEE YOU NEXT TIME!!!", 8500);
                }

        }
    }

    public void add_money(int winnings) {
        money += winnings;
        money_label.setText(money.toString());
        if (money < 0) {
            story_packer("You are broke...");
            story_packer("Game Over.");
            story_packer("Restart the game to play again :p");
            image_id.setImage(image_defeat);
            roll_dice_button.setDisable(true);
            gameover = true;
        }
    }

    public void hack_increment_money(ActionEvent actionEvent) {
        money += 5100;
        money_label.setText(money.toString());
    }

    // completely useless since the button text won't update but every game needs a debug menu
    public void hack_increment_stage(ActionEvent actionEvent) {
        stage++;
    }

}

