# ========================================
# CÓDIGO MIPS GENERADO AUTOMÁTICAMENTE
# Compilador - Proyecto 3
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Variables del programa
    valor_var: .word 0
    i_var: .word 0
    t1_var: .word 0
    t2_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/ag_intermediate.txt
main:
    # Prólogo simplificado main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE i INT
    # i = 0
    li $t0, 0
    sw $t0, i_var

L1:
    # DECLARE valor INT
    # valor = i
    lw $t0, i_var
    sw $t0, valor_var

    # WRITE valor
    lw $a0, valor_var
    jal print_int
    la $a0, nl
    jal print_string

    # t1 = i + 1
    lw $t1, i_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, t1_var

    # i = t1
    lw $t0, t1_var
    sw $t0, i_var

    # t2 = i < 3
    lw $t1, i_var
    li $t2, 3
    slt $t0, $t1, $t2
    sw $t0, t2_var

    # IF t2 GOTO L1
    # Recalculando condición do-while
    lw $t1, i_var
    li $t2, 3
    slt $t0, $t1, $t2
    bne $t0, $zero, L1

L2:

# Epílogo simplificado main
exit_main:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    li $v0, 10
    syscall


# Salida del programa
exit_program:
    li $v0, 10
    syscall
# ========================================
# FUNCIONES DEL SISTEMA
# ========================================

print_int:
    li $v0, 1
    syscall
    jr $ra

print_string:
    li $v0, 4
    syscall
    jr $ra

read_int:
    li $v0, 5
    syscall
    jr $ra

read_float:
    li $v0, 6
    syscall
    jr $ra

