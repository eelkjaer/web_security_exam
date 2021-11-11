/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package web.widget;

import domain.user.User;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class Navbar {
  private final HttpServletRequest request;
  private final List<Item> items =
      List.of(
          new Item("Start", "/", "home", false, false),
          new Item("Opret bruger", "/Register", "user-plus", false, false),
          new Item("Log ind", "/Login", "sign-in", false, false),
          new Item("Min profil", "/UserPage", "book", true, false),
          new Item("Administrator", "/AdminPage", "address-book", true, true),
          new Item("Log ud", "/Logout", "sign-out", true, false));

  public Navbar(HttpServletRequest request) {
    this.request = request;
  }

  public List<Item> getItems() {
    List<Item> list = new ArrayList<>();
    User user = (User) request.getSession().getAttribute("user");


    if(user != null){
      if(user.isAdmin()){
        for(Item x: items){
          if(x.adminOnly || x.authorizedOnly) list.add(x);
        }
      } else {
        for(Item x: items){
          if(x.authorizedOnly && !x.adminOnly) list.add(x);
        }
      }
    } else {
      for(Item x: items){
        if(!x.authorizedOnly && !x.adminOnly) list.add(x);
      }
    }


    /*if (user != null) {
      if (user.isAdmin()) {
        for (Item x : items) {
          if (x.authorizedOnly) {
            list.add(x);
          }
        }
      } else {
        for (Item x : items) {
          if (!x.adminOnly && !x.authorizedOnly) {
            list.add(x);
          }
        }
      }
    } else {
      for (Item x : items) {
        if (!x.adminOnly && !x.authorizedOnly) {
          list.add(x);
        }
      }
    }*/
    return list;
  }

  public class Item {
    private final String name;
    private final String url;
    private final String icon;
    private final boolean authorizedOnly;
    private final boolean adminOnly;

    public Item(String name, String url, String icon, boolean authorizedOnly, boolean adminOnly) {
      this.name = name;
      this.url = url;
      this.authorizedOnly = authorizedOnly;
      this.adminOnly = adminOnly;
      this.icon = icon;
    }

    public String getUrl() {
      return url;
    }

    public String getName() {
      return name;
    }

    public boolean isActive() {
      return request.getRequestURI().endsWith(url);
    }

    public String getIcon() {
      return icon;
    }
  }
}
