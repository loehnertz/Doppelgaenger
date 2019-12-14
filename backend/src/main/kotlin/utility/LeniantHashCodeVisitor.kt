package utility

import com.github.javaparser.ast.*
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.comments.BlockComment
import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.ast.comments.LineComment
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.modules.*
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.type.*
import com.github.javaparser.ast.visitor.GenericVisitor
import com.github.javaparser.ast.visitor.Visitable
import model.CloneType


/**
 * This class is derived from <code>com.github.javaparser.ast.visitor.HashCodeVisitor</code>
 * to ignore <code>com.github.javaparser.ast.expr.NameExpr</code> in the <code>hashCode()</code>
 * calculation. Additionally, instead of Java it is written in Kotlin.
 */
class LeniantHashCodeVisitor private constructor(private var cloneType: CloneType) : GenericVisitor<Int?, Void?> {
    override fun visit(n: AnnotationDeclaration?, arg: Void?): Int? {
        return n!!.members.accept(this, arg)!! * 31 + n.modifiers.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: AnnotationMemberDeclaration?, arg: Void?): Int? {
        return (if (n!!.defaultValue.isPresent) n.defaultValue.get().accept(this, arg) else 0)!! * 31 + n.modifiers.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31 + n.type.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: ArrayAccessExpr?, arg: Void?): Int? {
        return n!!.index.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31
    }

    override fun visit(n: ArrayCreationExpr?, arg: Void?): Int? {
        return n!!.elementType.accept(this, arg)!! * 31 + (if (n.initializer.isPresent) n.initializer.get().accept(this, arg) else 0)!! * 31 + n.levels.accept(this, arg)!! * 31
    }

    override fun visit(n: ArrayCreationLevel?, arg: Void?): Int? {
        return n!!.annotations.accept(this, arg)!! * 31 + (if (n.dimension.isPresent) n.dimension.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: ArrayInitializerExpr?, arg: Void?): Int? {
        return n!!.values.accept(this, arg)!! * 31
    }

    override fun visit(n: ArrayType?, arg: Void?): Int? {
        return n!!.componentType.accept(this, arg)!! * 31 + n.origin.hashCode() * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: AssertStmt?, arg: Void?): Int? {
        return n!!.check.accept(this, arg)!! * 31 + (if (n.message.isPresent) n.message.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: AssignExpr?, arg: Void?): Int? {
        return n!!.operator.hashCode() * 31 + n.target.accept(this, arg)!! * 31 + n.value.accept(this, arg)!! * 31
    }

    override fun visit(n: BinaryExpr?, arg: Void?): Int? {
        return n!!.left.accept(this, arg)!! * 31 + n.operator.hashCode() * 31 + n.right.accept(this, arg)!! * 31
    }

    override fun visit(n: BlockComment?, arg: Void?): Int? {
        return 31
    }

    override fun visit(n: BlockStmt?, arg: Void?): Int? {
        return n!!.statements.accept(this, arg)!! * 31
    }

    override fun visit(n: BooleanLiteralExpr?, arg: Void?): Int? {
        return (if (n!!.isValue) 1 else 0) * 31
    }

    override fun visit(n: BreakStmt?, arg: Void?): Int? {
        return (if (n!!.label.isPresent) n.label.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: CastExpr?, arg: Void?): Int? {
        return n!!.expression.accept(this, arg)!! * 31 + n.type.accept(this, arg)!! * 31
    }

    override fun visit(n: CatchClause?, arg: Void?): Int? {
        return n!!.body.accept(this, arg)!! * 31 + n.parameter.accept(this, arg)!! * 31
    }

    override fun visit(n: CharLiteralExpr?, arg: Void?): Int? {
        return n!!.value.hashCode() * 31
    }

    override fun visit(n: ClassExpr?, arg: Void?): Int? {
        return n!!.type.accept(this, arg)!! * 31
    }

    override fun visit(n: ClassOrInterfaceDeclaration?, arg: Void?): Int? {
        return n!!.extendedTypes.accept(this, arg)!! * 31 + n.implementedTypes.accept(this, arg)!! * 31 + (if (n.isInterface) 1 else 0) * 31 + n.typeParameters.accept(this, arg)!! * 31 + n.members.accept(this, arg)!! * 31 + n.modifiers.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: ClassOrInterfaceType?, arg: Void?): Int? {
        if (cloneType == CloneType.ONE) return n!!.name.accept(this, arg)!! * 31 + (if (n.scope.isPresent) n.scope.get().accept(this, arg) else 0)!! * 31 + (if (n.typeArguments.isPresent) n.typeArguments.get().accept(this, arg) else 0)!! * 31 + n.annotations.accept(this, arg)!! * 31
        return 31
    }

    override fun visit(n: CompilationUnit?, arg: Void?): Int? {
        return n!!.imports.accept(this, arg)!! * 31 + (if (n.module.isPresent) n.module.get().accept(this, arg) else 0)!! * 31 + (if (n.packageDeclaration.isPresent) n.packageDeclaration.get().accept(this, arg) else 0)!! * 31 + n.types.accept(this, arg)!! * 31
    }

    override fun visit(n: ConditionalExpr?, arg: Void?): Int? {
        return n!!.condition.accept(this, arg)!! * 31 + n.elseExpr.accept(this, arg)!! * 31 + n.thenExpr.accept(this, arg)!! * 31
    }

    override fun visit(n: ConstructorDeclaration?, arg: Void?): Int? {
        return n!!.body.accept(this, arg)!! * 31 + n.modifiers.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31 + n.parameters.accept(this, arg)!! * 31 + (if (n.receiverParameter.isPresent) n.receiverParameter.get().accept(this, arg) else 0)!! * 31 + n.thrownExceptions.accept(this, arg)!! * 31 + n.typeParameters.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: ContinueStmt?, arg: Void?): Int? {
        return (if (n!!.label.isPresent) n.label.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: DoStmt?, arg: Void?): Int? {
        return n!!.body.accept(this, arg)!! * 31 + n.condition.accept(this, arg)!! * 31
    }

    override fun visit(n: DoubleLiteralExpr?, arg: Void?): Int? {
        return n!!.value.hashCode() * 31
    }

    override fun visit(n: EmptyStmt?, arg: Void?): Int? {
        return 0
    }

    override fun visit(n: EnclosedExpr?, arg: Void?): Int? {
        return n!!.inner.accept(this, arg)!! * 31
    }

    override fun visit(n: EnumConstantDeclaration?, arg: Void?): Int? {
        return n!!.arguments.accept(this, arg)!! * 31 + n.classBody.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: EnumDeclaration?, arg: Void?): Int? {
        return n!!.entries.accept(this, arg)!! * 31 + n.implementedTypes.accept(this, arg)!! * 31 + n.members.accept(this, arg)!! * 31 + n.modifiers.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: ExplicitConstructorInvocationStmt?, arg: Void?): Int? {
        return n!!.arguments.accept(this, arg)!! * 31 + (if (n.expression.isPresent) n.expression.get().accept(this, arg) else 0)!! * 31 + (if (n.isThis) 1 else 0) * 31 + (if (n.typeArguments.isPresent) n.typeArguments.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: ExpressionStmt?, arg: Void?): Int? {
        return n!!.expression.accept(this, arg)!! * 31
    }

    override fun visit(n: FieldAccessExpr?, arg: Void?): Int? {
        return n!!.name.accept(this, arg)!! * 31 + n.scope.accept(this, arg)!! * 31 + (if (n.typeArguments.isPresent) n.typeArguments.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: FieldDeclaration?, arg: Void?): Int? {
        return n!!.modifiers.accept(this, arg)!! * 31 + n.variables.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: ForStmt?, arg: Void?): Int? {
        return n!!.body.accept(this, arg)!! * 31 + (if (n.compare.isPresent) n.compare.get().accept(this, arg) else 0)!! * 31 + n.initialization.accept(this, arg)!! * 31 + n.update.accept(this, arg)!! * 31
    }

    override fun visit(n: ForEachStmt?, arg: Void?): Int? {
        return n!!.body.accept(this, arg)!! * 31 + n.iterable.accept(this, arg)!! * 31 + n.variable.accept(this, arg)!! * 31
    }

    override fun visit(n: IfStmt?, arg: Void?): Int? {
        return n!!.condition.accept(this, arg)!! * 31 + (if (n.elseStmt.isPresent) n.elseStmt.get().accept(this, arg) else 0)!! * 31 + n.thenStmt.accept(this, arg)!! * 31
    }

    override fun visit(n: ImportDeclaration?, arg: Void?): Int? {
        return (if (n!!.isAsterisk) 1 else 0) * 31 + (if (n.isStatic) 1 else 0) * 31 + n.name.accept(this, arg)!! * 31
    }

    override fun visit(n: InitializerDeclaration?, arg: Void?): Int? {
        return n!!.body.accept(this, arg)!! * 31 + (if (n.isStatic) 1 else 0) * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: InstanceOfExpr?, arg: Void?): Int? {
        return n!!.expression.accept(this, arg)!! * 31 + n.type.accept(this, arg)!! * 31
    }

    override fun visit(n: IntegerLiteralExpr?, arg: Void?): Int? {
        return n!!.value.hashCode() * 31
    }

    override fun visit(n: IntersectionType?, arg: Void?): Int? {
        return n!!.elements.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: JavadocComment?, arg: Void?): Int? {
        return 31
    }

    override fun visit(n: LabeledStmt?, arg: Void?): Int? {
        return n!!.label.accept(this, arg)!! * 31 + n.statement.accept(this, arg)!! * 31
    }

    override fun visit(n: LambdaExpr?, arg: Void?): Int? {
        return n!!.body.accept(this, arg)!! * 31 + (if (n.isEnclosingParameters) 1 else 0) * 31 + n.parameters.accept(this, arg)!! * 31
    }

    override fun visit(n: LineComment?, arg: Void?): Int? {
        return 31
    }

    override fun visit(n: LocalClassDeclarationStmt?, arg: Void?): Int? {
        return n!!.classDeclaration.accept(this, arg)!! * 31
    }

    override fun visit(n: LongLiteralExpr?, arg: Void?): Int? {
        return n!!.value.hashCode() * 31
    }

    override fun visit(n: MarkerAnnotationExpr?, arg: Void?): Int? {
        return n!!.name.accept(this, arg)!! * 31
    }

    override fun visit(n: MemberValuePair?, arg: Void?): Int? {
        return n!!.name.accept(this, arg)!! * 31 + n.value.accept(this, arg)!! * 31
    }

    override fun visit(n: MethodCallExpr?, arg: Void?): Int? {
        return n!!.arguments.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31 + (if (n.scope.isPresent) n.scope.get().accept(this, arg) else 0)!! * 31 + (if (n.typeArguments.isPresent) n.typeArguments.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: MethodDeclaration?, arg: Void?): Int? {
        return (if (n!!.body.isPresent) n.body.get().accept(this, arg) else 0)!! * 31 + n.type.accept(this, arg)!! * 31 + n.modifiers.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31 + n.parameters.accept(this, arg)!! * 31 + (if (n.receiverParameter.isPresent) n.receiverParameter.get().accept(this, arg) else 0)!! * 31 + n.thrownExceptions.accept(this, arg)!! * 31 + n.typeParameters.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: MethodReferenceExpr?, arg: Void?): Int? {
        return n!!.identifier.hashCode() * 31 + n.scope.accept(this, arg)!! * 31 + (if (n.typeArguments.isPresent) n.typeArguments.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: NameExpr?, arg: Void?): Int? {
        if (cloneType == CloneType.ONE) return n!!.name.accept<Int, Void>(this, arg) * 31
        return 31
    }

    override fun visit(n: Name?, arg: Void?): Int? {
        return n!!.identifier.hashCode() * 31 + (if (n.qualifier.isPresent) n.qualifier.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: NodeList<*>?, arg: Void?): Int? {
        var result = 0
        for (node in n!!) {
            result += 31 * (node as Visitable).accept(this, arg)!!
        }
        return result
    }

    override fun visit(n: NormalAnnotationExpr?, arg: Void?): Int? {
        return n!!.pairs.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31
    }

    override fun visit(n: NullLiteralExpr?, arg: Void?): Int? {
        return 0
    }

    override fun visit(n: ObjectCreationExpr?, arg: Void?): Int? {
        return (if (n!!.anonymousClassBody.isPresent) n.anonymousClassBody.get().accept(this, arg) else 0)!! * 31 + n.arguments.accept(this, arg)!! * 31 + (if (n.scope.isPresent) n.scope.get().accept(this, arg) else 0)!! * 31 + n.type.accept(this, arg)!! * 31 + (if (n.typeArguments.isPresent) n.typeArguments.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: PackageDeclaration?, arg: Void?): Int? {
        return n!!.annotations.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31
    }

    override fun visit(n: Parameter?, arg: Void?): Int? {
        return n!!.annotations.accept(this, arg)!! * 31 + (if (n.isVarArgs) 1 else 0) * 31 + n.modifiers.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31 + n.type.accept(this, arg)!! * 31 + n.varArgsAnnotations.accept(this, arg)!! * 31
    }

    override fun visit(n: PrimitiveType?, arg: Void?): Int? {
        if (cloneType == CloneType.ONE) return n!!.type.hashCode() * 31 + n.annotations.accept(this, arg)!! * 31
        return 31
    }

    override fun visit(n: ReturnStmt?, arg: Void?): Int? {
        return (if (n!!.expression.isPresent) n.expression.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: SimpleName?, arg: Void?): Int? {
        if (cloneType == CloneType.ONE) return n!!.identifier.hashCode() * 31
        return 31
    }

    override fun visit(n: SingleMemberAnnotationExpr?, arg: Void?): Int? {
        return n!!.memberValue.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31
    }

    override fun visit(n: StringLiteralExpr?, arg: Void?): Int? {
        return n!!.value.hashCode() * 31
    }

    override fun visit(n: SuperExpr?, arg: Void?): Int? {
        return (if (n!!.typeName.isPresent) n.typeName.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: SwitchEntry?, arg: Void?): Int? {
        return n!!.labels.accept(this, arg)!! * 31 + n.statements.accept(this, arg)!! * 31 + n.type.hashCode() * 31
    }

    override fun visit(n: SwitchStmt?, arg: Void?): Int? {
        return n!!.entries.accept(this, arg)!! * 31 + n.selector.accept(this, arg)!! * 31
    }

    override fun visit(n: SynchronizedStmt?, arg: Void?): Int? {
        return n!!.body.accept(this, arg)!! * 31 + n.expression.accept(this, arg)!! * 31
    }

    override fun visit(n: ThisExpr?, arg: Void?): Int? {
        return (if (n!!.typeName.isPresent) n.typeName.get().accept(this, arg) else 0)!! * 31
    }

    override fun visit(n: ThrowStmt?, arg: Void?): Int? {
        return n!!.expression.accept(this, arg)!! * 31
    }

    override fun visit(n: TryStmt?, arg: Void?): Int? {
        return n!!.catchClauses.accept(this, arg)!! * 31 + (if (n.finallyBlock.isPresent) n.finallyBlock.get().accept(this, arg) else 0)!! * 31 + n.resources.accept(this, arg)!! * 31 + n.tryBlock.accept(this, arg)!! * 31
    }

    override fun visit(n: TypeExpr?, arg: Void?): Int? {
        return n!!.type.accept(this, arg)!! * 31
    }

    override fun visit(n: TypeParameter?, arg: Void?): Int? {
        return n!!.name.accept(this, arg)!! * 31 + n.typeBound.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: UnaryExpr?, arg: Void?): Int? {
        return n!!.expression.accept(this, arg)!! * 31 + n.operator.hashCode() * 31
    }

    override fun visit(n: UnionType?, arg: Void?): Int? {
        return n!!.elements.accept(this, arg)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: UnknownType?, arg: Void?): Int? {
        return n!!.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: VariableDeclarationExpr?, arg: Void?): Int? {
        return n!!.annotations.accept(this, arg)!! * 31 + n.modifiers.accept(this, arg)!! * 31 + n.variables.accept(this, arg)!! * 31
    }

    override fun visit(n: VariableDeclarator?, arg: Void?): Int? {
        return (if (n!!.initializer.isPresent) n.initializer.get().accept(this, arg) else 0)!! * 31 + n.name.accept(this, arg)!! * 31 + n.type.accept(this, arg)!! * 31
    }

    override fun visit(n: VoidType?, arg: Void?): Int? {
        return n!!.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: WhileStmt?, arg: Void?): Int? {
        return n!!.body.accept(this, arg)!! * 31 + n.condition.accept(this, arg)!! * 31
    }

    override fun visit(n: WildcardType?, arg: Void?): Int? {
        return (if (n!!.extendedType.isPresent) n.extendedType.get().accept(this, arg) else 0)!! * 31 + (if (n.superType.isPresent) n.superType.get().accept(this, arg) else 0)!! * 31 + n.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: ModuleDeclaration?, arg: Void?): Int? {
        return n!!.annotations.accept(this, arg)!! * 31 + n.directives.accept(this, arg)!! * 31 + (if (n.isOpen) 1 else 0) * 31 + n.name.accept(this, arg)!! * 31
    }

    override fun visit(n: ModuleRequiresDirective?, arg: Void?): Int? {
        return n!!.modifiers.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31
    }

    override fun visit(n: ModuleExportsDirective?, arg: Void?): Int? {
        return n!!.moduleNames.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31
    }

    override fun visit(n: ModuleProvidesDirective?, arg: Void?): Int? {
        return n!!.name.accept(this, arg)!! * 31 + n.with.accept(this, arg)!! * 31
    }

    override fun visit(n: ModuleUsesDirective?, arg: Void?): Int? {
        return n!!.name.accept(this, arg)!! * 31
    }

    override fun visit(n: ModuleOpensDirective?, arg: Void?): Int? {
        return n!!.moduleNames.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31
    }

    override fun visit(n: UnparsableStmt?, arg: Void?): Int? {
        return 0
    }

    override fun visit(n: ReceiverParameter?, arg: Void?): Int? {
        return n!!.annotations.accept(this, arg)!! * 31 + n.name.accept(this, arg)!! * 31 + n.type.accept(this, arg)!! * 31
    }

    override fun visit(n: VarType?, arg: Void?): Int? {
        return n!!.annotations.accept(this, arg)!! * 31
    }

    override fun visit(n: Modifier?, arg: Void?): Int? {
        return n!!.keyword.hashCode() * 31
    }

    override fun visit(n: SwitchExpr?, arg: Void?): Int? {
        return n!!.entries.accept(this, arg)!! * 31 + n.selector.accept(this, arg)!! * 31
    }

    override fun visit(n: YieldStmt?, arg: Void?): Int? {
        return n!!.expression.accept(this, arg)!! * 31
    }

    override fun visit(n: TextBlockLiteralExpr?, arg: Void?): Int? {
        return n!!.value.hashCode() * 31
    }

    companion object {
        private val SINGLETON: LeniantHashCodeVisitor = LeniantHashCodeVisitor(cloneType = CloneType.ONE)

        fun hashCode(node: Node): Int {
            return node.accept(SINGLETON, null)!!
        }

        fun setCloneType(cloneType: CloneType) {
            SINGLETON.cloneType = cloneType
        }
    }
}
