import numpy as np
from collections import Counter
import graphviz
from sklearn.datasets import load_iris
from sklearn.model_selection import train_test_split

# ==========================================
# PART 1: SCHOOL ASSIGNMENT (AST DIAGRAM)
# ==========================================
def generate_ast_diagram():
    print("Generating AST diagram for school assignment...")
    # Create a directed graph
    dot = graphviz.Digraph(comment='Abstract Syntax Tree', format='pdf')
    
    # Define the nodes (Root, Internal, Leaves)
    dot.node('root', '*', shape='circle', style='filled', fillcolor='lightblue')
    dot.node('plus', '+', shape='circle', style='filled', fillcolor='lightblue')
    dot.node('five', '5', shape='box', style='filled', fillcolor='lightgreen')
    dot.node('three', '3', shape='box', style='filled', fillcolor='lightgreen')
    dot.node('two', '2', shape='box', style='filled', fillcolor='lightgreen')
    
    # Define the edges (Parent -> Child)
    dot.edge('root', 'plus', label='L')
    dot.edge('root', 'two', label='R')
    dot.edge('plus', 'five', label='L')
    dot.edge('plus', 'three', label='R')
    
    # Render and open
    dot.render('ast_diagram', view=True)
    print("AST diagram generated! Check the PDF that just opened.\n")


# ==========================================
# PART 2: MACHINE LEARNING (DECISION TREE)
# ==========================================
class Node:
    def __init__(self, feature=None, threshold=None, left=None, right=None, *, value=None):
        self.feature = feature
        self.threshold = threshold
        self.left = left
        self.right = right
        self.value = value

    def is_leaf_node(self):
        return self.value is not None

def gini_impurity(y):
    if len(y) == 0:
        return 0.0
    _, counts = np.unique(y, return_counts=True)
    probs = counts / len(y)
    return 1 - np.sum(probs ** 2)

def find_best_split(X, y):
    best_gini = float('inf')
    best_feature = None
    best_threshold = None
    n_features = X.shape[1]

    for feature_idx in range(n_features):
        thresholds = np.unique(X[:, feature_idx])
        for threshold in thresholds:
            left_mask = X[:, feature_idx] <= threshold
            right_mask = X[:, feature_idx] > threshold
            
            y_left, y_right = y[left_mask], y[right_mask]
            if len(y_left) == 0 or len(y_right) == 0:
                continue

            n_left, n_right = len(y_left), len(y_right)
            n_total = n_left + n_right
            
            gini_left = gini_impurity(y_left)
            gini_right = gini_impurity(y_right)
            
            weighted_gini = (n_left / n_total) * gini_left + (n_right / n_total) * gini_right

            if weighted_gini < best_gini:
                best_gini = weighted_gini
                best_feature = feature_idx
                best_threshold = threshold

    return best_feature, best_threshold

class DecisionTree:
    def __init__(self, max_depth=5):
        self.max_depth = max_depth
        self.root = None

    def fit(self, X, y):
        self.root = self._build_tree(X, y, depth=0)

    def _build_tree(self, X, y, depth):
        n_samples, n_features = X.shape
        n_labels = len(np.unique(y))

        # Base cases
        if depth >= self.max_depth or n_labels == 1 or n_samples < 2:
            leaf_value = Counter(y).most_common(1)[0][0]
            return Node(value=leaf_value)

        best_feature, best_threshold = find_best_split(X, y)
        if best_feature is None:
            leaf_value = Counter(y).most_common(1)[0][0]
            return Node(value=leaf_value)

        left_mask = X[:, best_feature] <= best_threshold
        right_mask = X[:, best_feature] > best_threshold
        
        left_child = self._build_tree(X[left_mask], y[left_mask], depth + 1)
        right_child = self._build_tree(X[right_mask], y[right_mask], depth + 1)

        return Node(feature=best_feature, threshold=best_threshold, left=left_child, right=right_child)

    def predict(self, X):
        return np.array([self._traverse_tree(x, self.root) for x in X])

    def _traverse_tree(self, x, node):
        if node.is_leaf_node():
            return node.value
        if x[node.feature] <= node.threshold:
            return self._traverse_tree(x, node.left)
        return self._traverse_tree(x, node.right)

def visualize_ml_tree(tree, feature_names, class_names):
    print("Generating Graphviz visualization for ML model...")
    dot = graphviz.Digraph(comment='Decision Tree', format='pdf')
    
    def traverse(node, node_id):
        if node.is_leaf_node():
            label = f'Class: {class_names[node.value]}'
            dot.node(str(node_id), label, shape='box', style='filled', color='lightblue')
        else:
            label = f'{feature_names[node.feature]} <= {node.threshold:.2f}'
            dot.node(str(node_id), label, shape='ellipse')
            
            left_id = f"{node_id}_L"
            right_id = f"{node_id}_R"
            
            dot.edge(str(node_id), str(left_id), label="True")
            traverse(node.left, left_id)
            
            dot.edge(str(node_id), str(right_id), label="False")
            traverse(node.right, right_id)

    traverse(tree.root, 0)
    dot.render('decision_tree_output', format='pdf', view=True)
    print("ML Tree generated! Check the PDF that just opened.")


# ==========================================
# PART 3: RUNNING EVERYTHING
# ==========================================
if __name__ == "__main__":
    # 1. Run the AST Generator for your school assignment
    generate_ast_diagram()

    # 2. Run the Machine Learning Pipeline
    iris = load_iris()
    X = iris.data
    y = iris.target
    feature_names = iris.feature_names
    class_names = iris.target_names

    # Split data (80% train, 20% test)
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    print("Training custom Decision Tree...")
    tree = DecisionTree(max_depth=3)
    tree.fit(X_train, y_train)

    predictions = tree.predict(X_test)
    
    # Calculate REAL accuracy
    accuracy = np.mean(predictions == y_test)
    print(f"Accuracy on UNSEEN test data: {accuracy * 100:.2f}%")

    # 3. Visualize the trained tree
    visualize_ml_tree(tree, feature_names, class_names)