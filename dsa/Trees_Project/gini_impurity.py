import numpy as np
from collections import Counter
import graphviz
from sklearn.datasets import load_iris
from sklearn.model_selection import train_test_split

# ==========================================
# 1. THE NODE CLASS
# ==========================================
class Node:
    def __init__(self, feature=None, threshold=None, left=None, right=None, *, value=None):
        self.feature = feature
        self.threshold = threshold
        self.left = left
        self.right = right
        self.value = value  # This is only set for leaf nodes

    def is_leaf_node(self):
        return self.value is not None

# ==========================================
# 2. GINI IMPURITY & SPLITTING MATH
# ==========================================
def gini_impurity(y):
    """Calculates Gini impurity for a set of labels."""
    if len(y) == 0:
        return 0.0
    _, counts = np.unique(y, return_counts=True)
    probs = counts / len(y)
    return 1 - np.sum(probs ** 2)

def find_best_split(X, y):
    """Loops through features and thresholds to find the best split."""
    best_gini = float('inf')
    best_feature = None
    best_threshold = None
    
    n_features = X.shape[1]

    # Loop through every feature
    for feature_idx in range(n_features):
        # Get unique values in this column to use as potential thresholds
        thresholds = np.unique(X[:, feature_idx])
        
        # Loop through every threshold
        for threshold in thresholds:
            # Split the data
            left_mask = X[:, feature_idx] <= threshold
            right_mask = X[:, feature_idx] > threshold
            
            y_left, y_right = y[left_mask], y[right_mask]
            
            # Skip if a split results in an empty side
            if len(y_left) == 0 or len(y_right) == 0:
                continue

            # Calculate weighted Gini
            n_left, n_right = len(y_left), len(y_right)
            n_total = n_left + n_right
            
            gini_left = gini_impurity(y_left)
            gini_right = gini_impurity(y_right)
            
            weighted_gini = (n_left / n_total) * gini_left + (n_right / n_total) * gini_right

            # Update best split if we found a better one
            if weighted_gini < best_gini:
                best_gini = weighted_gini
                best_feature = feature_idx
                best_threshold = threshold

    return best_feature, best_threshold

# ==========================================
# 3. THE DECISION TREE CLASS
# ==========================================
class DecisionTree:
    def __init__(self, max_depth=5):
        self.max_depth = max_depth
        self.root = None

    def fit(self, X, y):
        self.root = self._build_tree(X, y, depth=0)

    def _build_tree(self, X, y, depth):
        n_samples, n_features = X.shape
        n_labels = len(np.unique(y))

        # Base cases (Stop conditions)
        if depth >= self.max_depth or n_labels == 1 or n_samples < 2:
            leaf_value = Counter(y).most_common(1)[0][0]
            return Node(value=leaf_value)

        # Find best split
        best_feature, best_threshold = find_best_split(X, y)

        # If no split improves the Gini score (or no split found), stop
        if best_feature is None:
            leaf_value = Counter(y).most_common(1)[0][0]
            return Node(value=leaf_value)

        # Split the data
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

# ==========================================
# 4. GRAPHVIZ VISUALIZER
# ==========================================
def visualize_tree(tree, feature_names, class_names):
    dot = graphviz.Digraph(comment='Decision Tree')
    
    def traverse(node, node_id):
        if node.is_leaf_node():
            # Leaf node: draw a box
            label = f'Class: {class_names[node.value]}'
            dot.node(str(node_id), label, shape='box', style='filled', color='lightblue')
        else:
            # Internal node: draw a circle and split edges
            label = f'{feature_names[node.feature]} <= {node.threshold:.2f}'
            dot.node(str(node_id), label, shape='ellipse')
            
            # Recursively traverse left and right
            left_id = f"{node_id}_L"
            right_id = f"{node_id}_R"
            
            dot.edge(str(node_id), str(left_id), label="True")
            traverse(node.left, left_id)
            
            dot.edge(str(node_id), str(right_id), label="False")
            traverse(node.right, right_id)

    traverse(tree.root, 0)
    
    # Render to a PDF and open it
    dot.render('decision_tree_output', format='pdf', view=True)

# ==========================================
# 5. RUNNING IT
# ==========================================
if __name__ == "__main__":
    # Load the Iris dataset (classic beginner ML dataset)
    iris = load_iris()
    X = iris.data
    y = iris.target
    feature_names = iris.feature_names
    class_names = iris.target_names

    # Train the tree
    print("Training custom Decision Tree...")
    tree = DecisionTree(max_depth=3)
    tree.fit(X, y)

    # Predict and check accuracy
    predictions = tree.predict(X)
    accuracy = np.mean(predictions == y)
    print(f"Accuracy on training data: {accuracy * 100:.2f}%")

    # Visualize
    print("Generating Graphviz visualization...")
    visualize_tree(tree, feature_names, class_names)
    print("Done! Check the PDF that just opened.")