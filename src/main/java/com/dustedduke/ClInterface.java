package com.dustedduke;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Пользовательский интерфейс. добавление, удаление и изменение параметров RSS/Atom feed.
 */

public class ClInterface {

    private static Scanner in;
    private static final String messageFormat = "\t%-50s%s%n";
    private FeedManager feedManager;

    public ClInterface(FeedManager feedManager) {

        this.feedManager = feedManager;
    }

    /**
     * Вывод приветственного сообщения
     */
    private void showWelcomeMessage() {

        System.out.println("***RSS reader***");
        System.out.println("type 'help' for help");

    }

    /**
     * Вывод help
     */
    private void showHelpMessage() {

        System.out.printf(messageFormat, "add url [file] [updatePeriod (PnDTnHnMn.nS.)]" ,"add new feed");
        System.out.printf(messageFormat, "rm [url]", "stop feed subscription");
        System.out.printf(messageFormat, "list", "show all subscriptions and parameters");
        System.out.printf(messageFormat, "params", "go to parameters menu");
        System.out.printf(messageFormat, "exit", "exit");
    }

    /**
     * Вывод меню опций
     */
    private void showOptionsMessage() {

        System.out.printf(messageFormat, "par [url]", "show current feed parameters");
        //TODO Изменение параметров feed
        System.out.printf(messageFormat, "setPeriod [url] [freq]", "set new period");
        System.out.printf(messageFormat, "setFields [url] [fields]", "choose file to store feed");
        System.out.printf(messageFormat, "setFile [url] [file]", "choose file to store feed");
        System.out.printf(messageFormat, "exit", "exit");

    }

    public void start() {

        showWelcomeMessage();
        in = new Scanner(System.in);
        readMainOptions();

    }

    /**
     * Обработка опций основного меню
     */
    public void readMainOptions() {

        String input = "";

        while (!input.equalsIgnoreCase("exit")) {
            try {

                System.out.print(">> ");
                input = in.nextLine();
                String[] splittedInputArray = input.split("\\s+");
                List<String> splittedInput = Arrays.asList(splittedInputArray);

                if (splittedInput.get(0).equals("add")) {

                    URL url = new URL(splittedInput.get(1));
                    String name;
                    String fileName;
                    Duration updatePeriod;
                    Set<String> fields = new HashSet<>();

                    if(splittedInput.size() == 3) {
                        fileName = splittedInput.get(2);
                        name = Integer.toString(url.toString().hashCode());
                    } else {
                        // TODO или вызов более простой функции
                        fileName = Integer.toString(url.toString().hashCode());
                        name = fileName;
                    }

                    System.out.println("Starting creation");
                    Set<String> availableTags = feedManager.testConnection(url);

                    if(availableTags != null) {

                        System.out.println("\nAvailable tags: ");
                        System.out.println("_____________________\n");
                        for(String tag: availableTags) {
                            System.out.println(tag);
                        }

                        System.out.println("\n_____________________");

                        // TODO проверка на правильный ввод

                        System.out.println("Choose tags from above (separate with space): ");
                        input = "";
                        input = in.nextLine();

                        if(input != "") {

                            fields = new HashSet<String>(Arrays.asList(input.split("\\s+")));

                        } else {

                            fields.add("title");
                            fields.add("contents");

                        }


                    } else {
                        System.out.println("Cannot establish connection. Try again later.");
                        continue;
                    }

                    System.out.println("Enter update period in format [PnDTnHnMn.nS] or Press [Enter] to skip.");

                    input = "";
                    input = in.nextLine();
                    System.out.println(input);

                    if(!input.isEmpty()) {
                        updatePeriod = Duration.parse(input);
                    } else {
                        updatePeriod = Duration.parse("PT20.345S");
                    }

                    System.out.println("Adding new feed...");
                    feedManager.subscribeTo(url, name, fileName, fields, updatePeriod);

                } else if (splittedInput.get(0).equals("rm")) {

                    if(splittedInput.size() == 2) {
                        feedManager.unsubscribeFrom(new URL(splittedInput.get(1)));
                    } else {
                        System.out.println("Wrong input. Try again.");
                        continue;
                    }

                } else if (splittedInput.get(0).equals("list")) {

                    // do something else
                    Map<String, String> feedsInfo = feedManager.getAllFeedInfo();
                    for (Map.Entry<String, String> entry : feedsInfo.entrySet()) {

                        System.out.print(entry.getKey() + ":\t");
                        JSONObject obj = new JSONObject(entry.getValue());

                        for(Iterator iterator = obj.keySet().iterator(); iterator.hasNext();) {
                            String key = (String) iterator.next();
                            System.out.print(obj.get(key) + "\t");
                        }

                        System.out.println("\n");

                    }


                } else if (splittedInput.get(0).equals("params")) {

                    showOptionsMessage();
                    readSettingsOptions();

                } else if (splittedInput.get(0).equals("help")) {
                    showHelpMessage();
                }

            } catch (InputMismatchException e) {
                System.out.println("Try again.");
                continue;

            } catch (MalformedURLException e) {
                System.out.println("Malformed URL. Try again.");
                continue;
            }
        }

    feedManager.stopAllThreads();

    }

    /**
     * Обработка опций меню настроек
     */
    public void readSettingsOptions() {
        String input = "";

        while (!input.equalsIgnoreCase("exit")) {

            try {

                System.out.print(">> ");
                input = in.nextLine();

                String[] splittedInputArray = input.split("\\s+");
                List<String> splittedInput = Arrays.asList(splittedInputArray);

                if (splittedInput.get(0).equals("par")) {
                    //do something
                } else if (splittedInput.get(0).equals("setPeriod")) {

                    // TODO достать все настройки
                    String url;
                    if (splittedInput.size() >= 3) {
                        url = splittedInput.get(1);

                        Feed feed = feedManager.getFeedFromURL(new URL(url));
                        if (feed != null) {

                            Duration newDur = Duration.parse(splittedInput.get(2));
                            feed.setUpdatePeriod(newDur);

                        } else {
                            System.out.println("Cannot find feed with URL: " + url);
                            continue;
                        }

                    } else {
                        System.out.println("Wrong input. Try again.");
                        continue;
                    }


                } else if (splittedInput.get(0).equals("setFields")) {


                    if (splittedInput.size() >= 3) {

                        String url = splittedInput.get(1);
                        Set<String> fields = new HashSet<String>(Arrays.asList(splittedInput.get(2).split("\\s+")));

                        Feed feed = feedManager.getFeedFromURL(new URL(url));
                        if (feed != null) {
                            feed.setItemsToRead(fields);
                        } else {
                            System.out.println("Cannot find feed with URL: " + url);
                            continue;
                        }

                        feed.setItemsToRead(fields);

                    } else {
                        System.out.println("Wrong input. Try again.");
                        continue;
                    }


                } else if (splittedInput.get(0).equals("setFile")) {
                    // TODO пока только с удалением фида и файла

                    URL url = new URL(splittedInput.get(1));
                    Feed feed = feedManager.getFeedFromURL(url);

                    String name = feed.getFeedName();
                    String fileName = feed.getFileName();
                    LocalDateTime lastUpdateDateTime = feed.getLastUpdatedateTime();
                    Duration updatePeriod = feed.getUpdatePeriod();
                    Set<String> itemsToRead = feed.getItemsToRead();

                    feedManager.unsubscribeFrom(url);
                    feedManager.subscribeTo(url, name, fileName, itemsToRead, lastUpdateDateTime, updatePeriod);
                }

            } catch (InputMismatchException e) {
                System.out.println("Try again.");
                System.out.print(">> ");
                input = in.nextLine();
            } catch (MalformedURLException e2) {
                System.out.println("Try again.");
                System.out.print(">> ");
                input = in.nextLine();
            }
        }
    }
}