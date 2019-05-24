import java.util.*;
public class ThreeAddress implements calVisitor
{
    private static int labelNumber = 1;

  public Object visit(SimpleNode node, Object data) {
     throw new RuntimeException("Visit SimpleNode");
  }

  public Object visit(Program node, Object data) {
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return data;
  }

  public Object visit(Var_Decl node, Object data) {
    String id = (String)node.jjtGetChild(0).jjtAccept(this, data);
    String type = (String) node.jjtGetChild(1).jjtAccept(this, data);
    if(node.jjtGetParent().toString().equals("Program")) {
        System.out.println( id);
    }
    return data;

  }
  public Object visit(Identifier node, Object data) {
    return node.value;
  }

  public Object visit(Const_Decl node, Object data) {
    String id = (String)node.jjtGetChild(0).jjtAccept(this, data);
    String type = (String) node.jjtGetChild(1).jjtAccept(this, data);
    String num = (String) node.jjtGetChild(2).jjtAccept(this, data);
    if(node.jjtGetParent().toString().equals("Program")) {
        System.out.println(id + "\t=\t" + num);
    }
    return data;
  }

  public Object visit(Main node, Object data) {
    System.out.println("MAIN:");
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return data;
  }

  public Object visit(Function node, Object data) {
    String name_of_function = (String) node.jjtGetChild(1).jjtAccept(this, data);
    int num = node.jjtGetNumChildren();
    int units = node.jjtGetChild(2).jjtGetNumChildren() * 4;

    System.out.println(name_of_function.toUpperCase() + ":\t");
    String ret = "\t\treturn ";

    for(int i = 0; i < num; i++) {
        if(node.jjtGetChild(i).toString().equals("FunctionRet")) {
            ret += node.jjtGetChild(i).jjtAccept(this, data);
        }
        else {
            node.jjtGetChild(i).jjtAccept(this, data);
        }
    }

    System.out.println(ret);
    return data;
  }

  public Object visit(Comp_Op node, Object data) {
      node.childrenAccept(this, data);
      return node.value;
  }


  public Object visit(Ret node, Object data) {
    return "";
  }

  public Object visit(FunctionRet node, Object data) {
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return node.value;
  }

  public Object visit(Type node, Object data) {
    return node.value;
  }

  public Object visit(Parameter_list node, Object data) {
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return data;
  }

  public Object visit(Statement node, Object data) {
    int after = 1;
    if(node.value != null) {
        if(node.value.equals("if") || node.value.equals("while")) {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> connects = new ArrayList<>();
        ArrayList<String> conditions = new ArrayList<>();
        for(int i = 0; i < node.jjtGetNumChildren(); i++) {
            String n = node.jjtGetChild(i).toString();
            if(n.equals("FunctionRet")) {
                ids.add((String)node.jjtGetChild(i).jjtAccept(this, data));
                after++;
            }
            else if(n.equals("Comp_Op")) {
                String value = (String)node.jjtGetChild(i).jjtGetChild(0).jjtAccept(this, data);
                String connect = (String)node.jjtGetChild(i).jjtGetChild(0).jjtAccept(this, data);
                String connection =  connect + " " + value;
                connects.add(connection);
                after++;
            }
            else if(n.equals("ANDOperator") || n.equals("OROperator")) {
                conditions.add((String)node.jjtGetChild(i).jjtAccept(this, data));
                after++;
            }
        }
        String result = "";
        for(int i = 0; i < ids.size(); i++)
        {
            result += ids.get(i) + " ";
            if(connects.size() > i) {
                result += connects.get(i);
            }
            if(conditions.size() > i) {

                result +=  " " +  conditions.get(conditions.size()-i-1) + " ";
            }
        }
        System.out.println("\tlabel " + labelNumber + ":");
        labelNumber++;
       }
    }
    int num = node.jjtGetNumChildren();

    if(num > 0) {
        String childrenNode = node.jjtGetChild(after).toString();
        String id = (String) node.jjtGetChild(after-1).jjtAccept(this, data);
        if(childrenNode.equals("FunctionRet")) {
            int n = node.jjtGetChild(after).jjtGetNumChildren();
            if(n > 0) {
                String name_of_function = (String) node.jjtGetChild(after).jjtAccept(this, data);

                int child = node.jjtGetChild(after).jjtGetChild(after-1).jjtGetNumChildren();
                Node children = node.jjtGetChild(after).jjtGetChild(after-1);
                int parameter_count = 0;
                for(int i = 0; i < child; i++)
                {
                    String parameter = (String) children.jjtGetChild(i).jjtAccept(this, data);
                    System.out.println("\t\tparam\t" + parameter);
                    parameter_count++;
                }
                System.out.println("\t\t" + id + "\t=\tcall " + name_of_function + ", " + parameter_count);
            }
            else {

                printOp(node, data);
            }
        }
        else if(childrenNode.equals("Arg_List")) {
            int child = node.jjtGetChild(after).jjtGetNumChildren();
            for(int i = 0; i < child; i++) {
                String parameter = (String)node.jjtGetChild(after).jjtGetChild(i).jjtAccept(this, data);

            }
           System.out.println("\t\tgoto\t" +  id);
        }
        else {
            String value = (String) node.jjtGetChild(after).jjtAccept(this, data);

            if(id != null && value != null) {
                System.out.println("\t\t" + id + "\t= " + value);
            }
        }
      }
    return node.value;

  }

  private void printOp(Statement node, Object data) {
    String id = (String) node.jjtGetChild(0).jjtAccept(this, data);

    String result = id + " = ";
    for(int i = 1; i < node.jjtGetNumChildren() - 1; i++)
    {
        result += " " + node.jjtGetChild(i).jjtAccept(this, data);
    }
    System.out.println("\t\t" + result);
  }
  public Object visit(Assignment node, Object data) {
    return data;
  }

  public Object visit(FunctionAssignment node, Object data) {
    return data;
  }

  public Object visit(AddOp node, Object data) {
    return "+" ;
  }

  public Object visit(MinusOp node, Object data) {
    if(node.jjtGetNumChildren() > 0)
    {
        return "-" + node.jjtGetChild(0).jjtAccept(this, data);
    }
    else
        return "-";
  }

  public Object visit(Num node, Object data) {
    return node.value;
  }

  public Object visit(Bool node, Object data) {
    return node.value;
  }


  public Object visit(EQOperator node, Object data) {
    return node.value;
  }

  public Object visit(NEQOperator node, Object data) {
    return node.value;
  }

  public Object visit(LTOperator node, Object data) {
    return node.value;
  }

  public Object visit(LTEOperator node, Object data) {
    return node.value;
  }

  public Object visit(GTOperator node, Object data) {
    return node.value;
  }

  public Object visit(GTEOperator node, Object data) {
    return node.value;
  }

  public Object visit(OROperator node, Object data) {
    return node.value;
  }

  public Object visit(ANDOperator node, Object data) {
    return node.value;
  }

  public Object visit(Arg_List node, Object data) {
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return node.value;
  }
}
