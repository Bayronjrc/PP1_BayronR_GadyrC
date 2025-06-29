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
    resultado_var: .word 0
    x_var: .word 0
    y_var: .word 0
    t1_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/c_intermediate.txt
main:
    # Prólogo de función
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE x INT
    # x = 5
    li $t0, 5
    sw $t0, x_var

    # DECLARE y INT
    # y = 3
    li $t0, 3
    sw $t0, y_var

    # t1 = x + y
    lw $t1, x_var
    lw $t2, y_var
    add $t0, $t1, $t2
    sw $t0, t1_var

    # DECLARE resultado INT
    # resultado = t1
    lw $t0, t1_var
    sw $t0, resultado_var

    # WRITE resultado
    lw $a0, resultado_var
    jal print_int
    la $a0, nl
    jal print_string


# Epílogo de función main
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

