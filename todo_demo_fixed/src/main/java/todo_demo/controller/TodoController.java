package todo_demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import todo_demo.entity.Todo;
import todo_demo.entity.User;
import todo_demo.repository.TodoRepository;
import todo_demo.repository.UserRepository;

@Controller
@RequestMapping("/todos")
public class TodoController {

    private final TodoRepository todoRepo;
    private final UserRepository userRepo;

    public TodoController(TodoRepository todoRepo, UserRepository userRepo) {
        this.todoRepo = todoRepo;
        this.userRepo = userRepo;
    }

    // TODO一覧
    @GetMapping
    public String listTodos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        List<Todo> todos = todoRepo.findByUser(user);
        model.addAttribute("todos", todos);
        return "todos";
    }

    // TODO追加
    @PostMapping("/add")
    public String addTodo(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam String task) {
        if (task == null || task.isBlank()) return "redirect:/todos";
        User user = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        Todo todo = new Todo();
        todo.setTask(task.trim());
        todo.setDone(false);
        todo.setUser(user);
        todoRepo.save(todo);
        return "redirect:/todos";
    }

    // 単一 TODO を完了にする
    @PostMapping("/done/{id}")
    public String markDone(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        todoRepo.findById(id).ifPresent(todo -> {
            if (todo.getUser().getId().equals(user.getId())) {
                todo.setDone(true);
                todoRepo.save(todo);
            }
        });
        return "redirect:/todos";
    }

    // 選択削除
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> deleteTodos(@RequestBody IdListRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
            List<Todo> todosToDelete = todoRepo.findAllById(request.getIds());
            todosToDelete.stream()
                         .filter(todo -> todo.getUser().getId().equals(user.getId()))
                         .forEach(todoRepo::delete);
            result.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
        }
        return result;
    }

    public static class IdListRequest {
        private List<Long> ids;
        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
    }

    // すべて完了/未完了切替
    @PostMapping("/toggleAll")
    @ResponseBody
    public Map<String, Object> toggleAll(@RequestBody ToggleAllRequest request,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
            List<Todo> todosToUpdate = todoRepo.findAllById(request.getIds());
            todosToUpdate.stream()
                         .filter(todo -> todo.getUser().getId().equals(user.getId()))
                         .forEach(todo -> {
                             todo.setDone(request.isDone());
                             todoRepo.save(todo);
                         });
            result.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
        }
        return result;
    }

    public static class ToggleAllRequest {
        private List<Long> ids;
        private boolean done;
        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
        public boolean isDone() { return done; }
        public void setDone(boolean done) { this.done = done; }
    }

    // チェックボックス即反映
@PostMapping("/toggle/{id}")
@ResponseBody
public Map<String,Object> toggleTodo(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails){
    Map<String,Object> result=new HashMap<>();
    try{
        User user=userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        todoRepo.findById(id).ifPresent(todo->{
            if(todo.getUser().getId().equals(user.getId())){
                todo.setDone(!todo.isDone());
                todoRepo.save(todo);
            }
        });
        result.put("success",true);
    }catch(Exception e){
        result.put("success",false);
    }
    return result;
}

// 編集
@PostMapping("/update/{id}")
@ResponseBody
public Map<String,Object> updateTodo(@PathVariable Long id,
                                     @RequestBody Map<String,String> body,
                                     @AuthenticationPrincipal UserDetails userDetails){
    Map<String,Object> result=new HashMap<>();
    try{
        User user=userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        todoRepo.findById(id).ifPresent(todo->{
            if(todo.getUser().getId().equals(user.getId())){
                todo.setTask(body.get("task"));
                todoRepo.save(todo);
            }
        });
        result.put("success",true);
    }catch(Exception e){
        result.put("success",false);
    }
    return result;
}

// 単体削除
@PostMapping("/delete/{id}")
@ResponseBody
public Map<String,Object> deleteTodo(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails){
    Map<String,Object> result=new HashMap<>();
    try{
        User user=userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        todoRepo.findById(id).ifPresent(todo->{
            if(todo.getUser().getId().equals(user.getId())){
                todoRepo.delete(todo);
            }
        });
        result.put("success",true);
    }catch(Exception e){
        result.put("success",false);
    }
    return result;
}

}
